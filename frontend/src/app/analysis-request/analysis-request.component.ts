import { Component, inject, OnInit } from '@angular/core';
import { AnalysisService } from '../services/analysis.service';
import { AnalysisRequest, AnalysisRequestDetails, AnalysisResultObjectDTO } from '../interfaces/analysis.interface';
import { Observable } from 'rxjs';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatExpansionModule } from '@angular/material/expansion';

import { ReactiveFormsModule, FormArray, FormGroup, FormControl, FormGroupDirective } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { MatButtonModule } from '@angular/material/button';

import {
  MatDialog,
  MAT_DIALOG_DATA,
  MatDialogTitle,
  MatDialogContent,
} from '@angular/material/dialog';

import { AnalysisRequestInfoDialogComponent } from './analysis-request-info-dialog.component';

interface DisplayValues {
  [key: number]: string;
}

interface Dictionary<T> {
    [Key: string]: T;
}

@Component({
  selector: 'app-analysis-request',
  standalone: true,
  imports: [
    MatTableModule,
    MatExpansionModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    MatButtonModule,
  ],
  templateUrl: './analysis-request.component.html',
  styleUrl: './analysis-request.component.scss'
})

export class AnalysisRequestComponent implements OnInit {

  private analysisService = inject(AnalysisService);

  analysisRequests$!: Observable<AnalysisRequest[]>
  dataSource!: MatTableDataSource<AnalysisRequest>;
  currentUsersAnalysisRequests: AnalysisRequest[] = [];

  displayedColumns = [
    "analysisTitle",
    "analysisType",
    "hpcAccount",
    "analysisStatus",
    "downloadResults",
    "deleteResults",
    "resumeAnalysis",
    "analysisInfo",
  ];

  statusIdDisplayValues: DisplayValues = {
    100: 'Tiek inicializēts projekts',
    101: 'Kļūda projekta inicializācijā',
    200: 'Sūta paraugu informāciju',
    201: 'Kļūda paraugu informācijas sūtīšanā',
    300: 'Inicializē darbaplūsmas skriptu',
    301: 'Kļūda darbaplūsmas skripta inicializēšanā',
    400: 'Uz HPC tiek lejuplādētas sekvences',
    401: 'Kļūda sekvenču lejuplādē',
    500: 'Tiek uzsākta analīze',
    501: 'Kļūda analīzē',
    600: 'No HPC tiek lejuplādēti rezultāti',
    601: 'Kļūda rezultātu lejuplādē no HPC',
    700: 'Rezultāti tiek augšuplādēti augšuplādēti portālā',
    701: 'Kļūda rezultātu augšuplādē',
    800: 'Tiek atbrīvota vieta uz HPC',
    801: 'Kļūda vietas atbrīvošanā uz HPC',
    900: 'Analīze ir pabeigta',
  }

  fileList$: { [key: string]: Observable<AnalysisResultObjectDTO[]> } = {};


  filesToDownload = new FormGroup({
    fileList:new FormArray([]),
  });

  analysisInfoDialog = inject(MatDialog);

  ngOnInit() {
    this.analysisRequests$ = this.analysisService.currentUserAnalysisRequests();
    this.analysisRequests$.subscribe(reqs => {
        console.log(reqs);
        this.dataSource = new MatTableDataSource(reqs);
        this.currentUsersAnalysisRequests = reqs;
        for (let req of reqs) {
          this.fileList$[req.id.toString()] = this.listObjects(req);
        }
        //this.dataSource.paginator = this.paginator;
        //this.dataSource.sort = this.sort;
    });
  }
  download(aResDTO: AnalysisResultObjectDTO) {
    //return this.analysisService.downloadAnalysisResults(aResDTO);
    this.analysisService.downloadAnalysisResults(aResDTO).subscribe((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = aResDTO.fileName;  // Specify the file name
      a.click();
      window.URL.revokeObjectURL(url);  // Clean up
    });
  }
  downloadAll(aReq: AnalysisRequest) {
    this.fileList$[aReq.id.toString()].subscribe((analysisResults: AnalysisResultObjectDTO[]) => {
      for (let aRes of analysisResults) {
        this.download(aRes);
      }
    });
  }
  listObjects(aReq: AnalysisRequest): Observable<AnalysisResultObjectDTO[]> {
    //this.analysisService.listAnalysisResults(aReq).subscribe((x) => {
      //console.log(x);
    //});
    return this.analysisService.listAnalysisResults(aReq);
  }
  deleteAnalysis(aReq: AnalysisRequest) {
    this.analysisService.deleteAnalysisResults(aReq).subscribe(() => {
      console.log(`deleting ${aReq.id}`);
      location.reload();
    });
  }
  resumeAnalysis(aReq: AnalysisRequest) {
    this.analysisService.resumeAnalysisRequest(aReq).subscribe(() => {
      console.log(`resuming ${aReq.id}`);
      location.reload();
    });
  }
  showAnalysisInfo(aReq: AnalysisRequest) {
    this.analysisInfoDialog.open(AnalysisRequestInfoDialogComponent, {
      data: aReq,
    });
    console.log(aReq);
  }

  deleteDummyAnalysis() {
    this.analysisService.sendDummyAnalysisDeleteRequest().subscribe(() => {
      console.log(`sending dummy delete request`);
      location.reload();
    });
  }
}
