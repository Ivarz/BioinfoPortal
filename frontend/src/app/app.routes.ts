import { Routes } from '@angular/router';
import { HomecompComponent } from './homecomp/homecomp.component';
import { SubmitNgsAnalysisComponent } from './submit-ngs-analysis/submit-ngs-analysis.component';
import { SequenceLookupComponent } from './sequence-lookup/sequence-lookup.component';
import { AnalysisRequestComponent } from './analysis-request/analysis-request.component';
import { HpcResourceAccountManagerComponent } from './hpc-resource-account-manager/hpc-resource-account-manager.component';

export const routes: Routes = [
  { path: '', redirectTo: '/submit-ngs-analysis', pathMatch: 'full' },
  { path: 'homecomp', component: HomecompComponent },
  { path: 'submit-ngs-analysis', component: SubmitNgsAnalysisComponent },
  { path: 'sequence-lookup', component: SequenceLookupComponent },
  { path: 'analysis-results', component: AnalysisRequestComponent },
  { path: 'hpc-resource-accounts', component: HpcResourceAccountManagerComponent },
];
