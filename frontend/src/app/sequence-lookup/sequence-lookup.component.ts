import { Component, inject, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { SequenceService, BatchSequenceQuery } from '../services/sequence.service';
import { Sequence } from '../interfaces/sequence.interface';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule, FormControl, FormGroup } from '@angular/forms';
import { debounceTime, shareReplay } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { DomSanitizer } from '@angular/platform-browser';
import { MatDividerModule } from '@angular/material/divider';
import { SubmittedSamplesService } from '../services/submitted-samples.service';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatSort, Sort, MatSortModule } from '@angular/material/sort';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { sequencesToTsv } from '../utils/utils';
import { SequenceDbMaintenanceService } from '../services/sequence-db-maintenance.service';

import {SelectionModel} from '@angular/cdk/collections';


@Component({
  selector: 'app-sequence-lookup',
  standalone: true,
  imports: [
    MatTableModule,
    MatPaginator,
    MatPaginatorModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatDividerModule,
    MatButtonModule,
    MatSortModule,
    MatCheckboxModule,
  ],
  templateUrl: './sequence-lookup.component.html',
  styleUrl: './sequence-lookup.component.scss'
})

export class SequenceLookupComponent implements OnInit {
  private sequenceService = inject(SequenceService);
  private submittedSamplesService = inject(SubmittedSamplesService)
  dataSource!: MatTableDataSource<Sequence>;
  batchDataSource!: MatTableDataSource<Sequence>;
  showResultsTable = false;

  displayedColumns: string[] = ['sampleId', 'runId', 'lane', 'barcode', 'type', 'mate1'];

  @ViewChild('searchPaginator') paginator!: MatPaginator;

  @ViewChild(MatSort) sort!: MatSort;

  fileUrl: any;

  data: string = '';
  blobObs$!: Observable<Blob>;

  private sanitizer = inject(DomSanitizer);

  private router = inject(Router);
  private sequenceDbMaintenanceService = inject(SequenceDbMaintenanceService);
  lockPage = false;

  selection = new SelectionModel<Sequence>(true, []);


  sequenceLookupForm = new FormGroup({
    sampleIds: new FormControl(''),
    runIds: new FormControl(''),
    lanes: new FormControl(''),
    types: new FormControl(''),
    fuzzySearch: new FormControl(false),
  });

  ngOnInit() {
    this.sequenceLookupForm.valueChanges.pipe(debounceTime(200)).subscribe(value => {
      this.sequenceDbMaintenanceService.maintenanceState().subscribe((seqdbMaintenance) => {
        this.lockPage = seqdbMaintenance.maintenanceModeOn;
      });
      let sampleIds: string[] = [];
      let runIds: string[] = [];
      let lanes: string[] = [];
      let types: string[] = [];
      let fuzzySearch: boolean = value.fuzzySearch || false;
      if (value.sampleIds) {
        sampleIds = value.sampleIds.split('\n');
      }
      if (value.runIds) {
        runIds = value.runIds.split('\n');
      }
      if (value.lanes) {
        lanes = value.lanes.split('\n');
      }
      if (value.types) {
        types = value.types.split('\n');
      }
      let query = { 'sampleIds': sampleIds, 'runIds':runIds, 'lanes':lanes, 'types': types, 'fuzzySearch': fuzzySearch };
      let fetchedSeq$ = this.sequenceService.batchFetchSequencesFull(query).pipe(shareReplay());
      fetchedSeq$.subscribe(sequences => {
        console.log("querying");
        this.dataSource = new MatTableDataSource(sequences);
        console.log('instantiate paginator');
        this.showResultsTable = sequences.length > 0;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
      fetchedSeq$.subscribe(sequences => {
        const tsvString = sequencesToTsv(sequences);
        const blob = new Blob([tsvString]);
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
      });

      fetchedSeq$.subscribe(sequences => {
        console.log("changing submitted samples");
        console.log(sequences);
        this.submittedSamplesService.changeSubmittedSamples(sequences);
        this.showResultsTable = sequences.length > 0;
      });
    });
    this.sequenceDbMaintenanceService.maintenanceState().subscribe((seqdbMaintenance) => {
      this.lockPage = seqdbMaintenance.maintenanceModeOn;
    });

  }
  onRedirectToNgsAnalysisSubmission() {
    this.router.navigate(['submit-ngs-analysis']);
  }

  /** Whether the number of selected elements matches the total number of rows. */
  //isAllSelected() {
    //const numSelected = this.selection.selected.length;
    //const numRows = this.dataSource.data.length;
    //return numSelected === numRows;
  //}

  //[>* Selects all rows if they are not all selected; otherwise clear selection. <]
  //masterToggle() {
    //this.isAllSelected() ?
        //this.selection.clear() :
        //this.dataSource.data.forEach(row => this.selection.select(row));
  //}

  //logSelection() {
    //this.selection.selected.forEach(s => console.log(s.sampleId));
  //}
}

