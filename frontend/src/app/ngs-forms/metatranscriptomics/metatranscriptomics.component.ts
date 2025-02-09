import { Component, OnInit, Input } from '@angular/core';

import { ReactiveFormsModule, FormGroup, FormControl, FormGroupDirective } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-metatranscriptomics',
  standalone: true,
  imports: [ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatFormFieldModule,
    MatCheckboxModule
  ],
  templateUrl: './metatranscriptomics.component.html',
  styleUrl: './metatranscriptomics.component.scss'
})
export class MetatranscriptomicsComponent implements OnInit {
  @Input() analysisParameters!: FormGroup;
  hosts = [
    {'id': 'GRCh38', 'displayName': 'Cilvēks (GRCh38)'},
    {'id': 'GRCm39', 'displayName': 'Pele (GRCm39)'},
    {'id': 'none', 'displayName': 'Nav'}
  ];

  taxonomicClassifiers = [
    { 'id': 'kraken2Uhgg', 'displayName': 'Kraken2 UHGG (confidence 0.1)' },
    {  'id': 'kraken2Pluspf', 'displayName': 'Kraken2 Standard + PF (confidence 0.1)' },
    {  'id': 'kraken2PluspfSpecific', 'displayName': 'Kraken2 Standard + PF (confidence 0.8)' },
    {  'id': 'metaphlan4', 'displayName': 'Metaphlan4' },
    {  'id': 'humann4', 'displayName': 'Humann4' },
  ]

  ngOnInit() {
    this.analysisParameters.addControl("host", new FormControl(''));
    this.analysisParameters.addControl("type", new FormControl("metatranscriptomics"));
    this.analysisParameters.addControl("kraken2Uhgg", new FormControl(false));
    this.analysisParameters.addControl("kraken2Pluspf", new FormControl(false));
    this.analysisParameters.addControl("kraken2PluspfSpecific", new FormControl(false));
    this.analysisParameters.addControl("metaphlan4", new FormControl(false));
    this.analysisParameters.addControl("humann4", new FormControl(false));
  }

}
