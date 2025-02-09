import { Component, OnInit, Input } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule, FormGroup, FormControl, FormRecord } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-transcriptomics',
  standalone: true,
  imports: [
    MatSelectModule,
    ReactiveFormsModule,
    MatCheckboxModule,
  ],
  templateUrl: './transcriptomics.component.html',
  styleUrl: './transcriptomics.component.scss'
})
export class TranscriptomicsComponent implements OnInit {
  @Input() analysisParameters!: FormGroup;
  starReferences = [
    {'id': 'GRCh38', 'displayName': 'Cilvēks (GRCh38)'},
    {'id': 'GRCm39', 'displayName': 'Pele (GRCm39)'},
    {'id': 'none', 'displayName': 'Nav'}
  ];

  ngOnInit() {
    this.analysisParameters.addControl('type', new FormControl('transcriptomics'));
    this.analysisParameters.addControl('starReference', new FormControl(''));
    this.analysisParameters.addControl('rRnaEstimation', new FormControl(false));
  }
}
