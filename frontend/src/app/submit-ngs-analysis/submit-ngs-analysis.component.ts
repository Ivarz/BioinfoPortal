import { Component, inject, OnInit, ViewChild} from '@angular/core';
import { Observable } from 'rxjs';
import { HpcAccountService } from '../services/hpc-account.service';
import { AnalysisService } from '../services/analysis.service';
import { AsyncPipe } from '@angular/common';

import { ReactiveFormsModule, FormGroup, FormControl, FormRecord, Validators } from '@angular/forms';

import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule, MatFormFieldControl } from '@angular/material/form-field';


import { UploadService } from '../services/upload.service';
import { SubmittedSamplesService } from '../services/submitted-samples.service';
import { Sequence } from '../interfaces/sequence.interface';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatInputModule } from '@angular/material/input';

import { MatButtonModule } from '@angular/material/button';

import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { shareReplay } from 'rxjs/operators';
import { DomSanitizer } from '@angular/platform-browser';

import { MetagenomicsComponent } from '../ngs-forms/metagenomics/metagenomics.component';
import { MetatranscriptomicsComponent } from '../ngs-forms/metatranscriptomics/metatranscriptomics.component';
import { TranscriptomicsComponent } from '../ngs-forms/transcriptomics/transcriptomics.component';
import { Router } from '@angular/router';
import { sequencesToTsv } from '../utils/utils';
import { SequenceDbMaintenanceService } from '../services/sequence-db-maintenance.service';
import { AnalysisType, AnalysisParameters } from '../interfaces/analysis.interface';
import { MatCheckboxModule } from '@angular/material/checkbox';

export interface NgsSubmissionInfo {
    hpcAccount: string,
    title: string,
    analysisType: AnalysisType,
    analysisParameters: AnalysisParameters,
    metagenomicAnalysisParameters: {
      host: string,
      kraken2Uhgg: boolean,
      kraken2Pluspf: boolean,
      kraken2PluspfSpecific: boolean,
      metaphlan4: boolean,
      humann4: boolean,
    },
    transcriptomicAnalysisParameters: {
      starReference: string,
    },
    metatranscriptomicAnalysisParameters: {
      host: string,
      kraken2Uhgg: boolean,
      kraken2Pluspf: boolean,
      kraken2PluspfSpecific: boolean,
      metaphlan4: boolean,
      humann4: boolean,
    },
}

@Component({
  selector: 'app-submit-ngs-analysis',
  standalone: true,
  imports: [
    AsyncPipe,
    ReactiveFormsModule,
    MatSelectModule,
    MatFormFieldModule,
    MatPaginatorModule,
    MatPaginator,
    MatTableModule,
    MatAutocompleteModule,
    MatInputModule,
    MatButtonModule,
    MetagenomicsComponent,
    MetatranscriptomicsComponent,
    TranscriptomicsComponent,
    MatCheckboxModule,
  ],
  templateUrl: './submit-ngs-analysis.component.html',
  styleUrl: './submit-ngs-analysis.component.scss'
})
export class SubmitNgsAnalysisComponent implements OnInit {
  private hpcAccountService = inject(HpcAccountService);
  accounts$!: Observable<any>;
  dataSource!: MatTableDataSource<{'first': Sequence, 'second': boolean}>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private uploadService = inject(UploadService);
  private sanitizer = inject(DomSanitizer);
  private submittedSamplesService = inject(SubmittedSamplesService)

  private analysisService = inject(AnalysisService);
  private router = inject(Router);
  private sequenceDbMaintenanceService = inject(SequenceDbMaintenanceService);
  lockPage = false;

  fileUrl: any;

  selectedFile: any;
  //selectedFileContent: string = '';
  sampleRequest: Sequence[] = [];
  sequencesToAnalyse: Sequence[] = [];

  displayedColumns: string[] = ['sampleId', 'runId', 'lane', 'barcode', 'type', 'mate1', 'mate2'];

  analysisTypes: {id: string, displayValue: string}[] = [
    {id: 'metagenomics', displayValue: 'Metagenomi'},
    {id: 'metatranscriptomics', displayValue: 'Metatranskriptomi'},
    {id: 'transcriptomics', displayValue: 'Transkriptomi'},
  ];

  showTable = false;


  ngOnInit() {
    this.accounts$ = this.hpcAccountService.fetchHpcAccounts();
    this.submittedSamplesService.currentlySubmittedSamples.subscribe(sequences => {
      let markedSequences = sequences.map(x => ({ "first": x, "second": x != null && x.mate1 != null && x.mate1.length > 0}));
      let foundSequences = markedSequences; //.filter(x => x.second)
      this.dataSource = new MatTableDataSource(foundSequences);
      this.dataSource.paginator = this.paginator;
      this.sequencesToAnalyse = markedSequences.filter(x => x.second).map(x => x.first);
      if (foundSequences.length > 0) {
        this.showTable = true;
      }
    });
    this.sequenceDbMaintenanceService.maintenanceState().subscribe((seqdbMaintenance) => {
      this.lockPage = seqdbMaintenance.maintenanceModeOn;
    });
  }

  fetchHpcAccounts() {
    this.accounts$ = this.hpcAccountService.fetchHpcAccounts();
  }

  updateHpcAccounts() {
    this.hpcAccountService.updateHpcAccounts();
  }

  ngsForm = new FormGroup({
    hpcAccount: new FormControl('', Validators.required),
    title: new FormControl(''),
    analysisType: new FormControl(''),
    analysisParameters: new FormGroup({}),
    keepHpcFiles: new FormControl(false),
  });

  onFileChange(event: any): void {
    this.sequenceDbMaintenanceService.maintenanceState().subscribe((seqdbMaintenance) => {
      this.lockPage = seqdbMaintenance.maintenanceModeOn;
    });
    this.showTable = true;
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const fileReader = new FileReader();
      fileReader.onload = () => {
        console.log((fileReader.result as string).split('\n').slice(1)
          .forEach((line) => {
            let fields = line.split('\t');
            if (fields.length > 0 && fields[0].length > 0) {
              this.sampleRequest.push({recordId: 0, sampleId: fields[0], runId: fields[1], lane: fields[2], barcode: '', type: '', mate1: '', mate2: ''});
            }
          }));
        this.onUpload();
      };

      fileReader.readAsText(this.selectedFile);
    }
  }

  onUpload(): void {
    if (this.sampleRequest.length > 0) {
      let fetchedSeq$ = this.uploadService.uploadSampleRequest(this.sampleRequest).pipe(shareReplay());
      fetchedSeq$.subscribe(response => {
        console.log(response);
        this.dataSource = new MatTableDataSource(response);
        this.dataSource.paginator = this.paginator;
      });
      fetchedSeq$.subscribe(sequences => {
        const tsvString = sequencesToTsv(
          sequences
            .map((x: {'first': Sequence, 'second': boolean }) => {
              x['first']
            }));
        const blob = new Blob([tsvString]);
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
      });
      fetchedSeq$.subscribe(sequences => {
        for (let seq of sequences) {
          if (seq['first'].mate1.length > 0) {
            this.sequencesToAnalyse.push(seq['first']);
          }
        }
      });
      fetchedSeq$.subscribe(sequences => {
        this.submittedSamplesService.changeSubmittedSamples(
          sequences.map((x:{'first': Sequence, 'second': boolean})  => x.first));
      });
    } else {
      console.error('No file selected');
    }
  }
  get formAsJson() {
    return JSON.stringify(this.ngsForm.getRawValue());
  }

  onSubmitAnalysis() {
    //this.ngsForm.addControl('sequences',new FormControl(this.sequencesToAnalyse));
    //let formValues = { 'parameters': this.ngsForm.getRawValue(), 'sequences': this.sequencesToAnalyse };
    let formValues: any = this.ngsForm.getRawValue();
    formValues.sequences = this.sequencesToAnalyse;

    console.log(formValues);
    let req$ = this.analysisService.submitAnalysisRequest(JSON.stringify(formValues));
    //this.submittedSamplesService.changeSubmittedSamples([]);
    //req$.next();
    req$.subscribe(x => {
      console.log(x)
    });
    this.router.navigate(['analysis-results']);
    window.scroll({
           top: 0,
           left: 0,
           behavior: 'smooth'
    });

  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}
