import { AnalysisRequest, AnalysisRequestDetails, AnalysisResultObjectDTO } from '../interfaces/analysis.interface';
import { Component, inject } from '@angular/core';

import {
  MatDialog,
  MAT_DIALOG_DATA,
  MatDialogTitle,
  MatDialogContent,
} from '@angular/material/dialog';

@Component({
  selector: 'app-analysis-request-info-dialog',
  templateUrl: 'analysis-request-info-dialog.component.html',
  standalone: true,
  imports: [MatDialogTitle, MatDialogContent],
})
export class AnalysisRequestInfoDialogComponent {
  data = inject(MAT_DIALOG_DATA);
}

