import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Sequence {
  recordId : number;
  sampleId : string;
  runId : string;
  lane : string;
  barcode : string;
  type : string;
  mate1 : string;
  mate2 : string;
}

export interface BatchSequenceQuery {
  sampleIds : string[];
  runIds : string[];
  lanes : string[];
  types : string[];
  fuzzySearch: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SequenceService {

  private http = inject(HttpClient);
  private readonly oidcSecurityService = inject(OidcSecurityService);

  constructor() { }
  fetchSequences(sample_id? : string | null | undefined,
    run_id? : string | null | undefined,
    lane? : string | null | undefined,
    type? : string | null | undefined,
  ): Observable<Sequence[]> {
    let params = [];
    if (sample_id) {
      params.push(`sample_id=${sample_id}`);
    }
    if (run_id) {
      params.push(`run_id=${run_id}`);
    }
    if (lane) {
      lane.replace('L0','');
      params.push(`lane=${lane}`);
    }
    if (type) {
      params.push(`type=${type}`);
    }
    let paramString = params.join('&');
    if (paramString.length > 0){
      paramString = '?'+paramString;
    }
    console.log(`paramString: ${paramString}`);
    return this.http.get<Sequence[]>(`${environment.apiUrl}/sequences/search${paramString}`);
  }

  batchFetchSequences(sampleIds? : string[] | null | undefined): Observable<Sequence[]> {
    return this.http.post<Sequence[]>(`${environment.apiUrl}/batch-sequence`, sampleIds);
  }
  batchFetchSequencesFull(query? : BatchSequenceQuery | null | undefined,
  ): Observable<Sequence[]> {
    console.log("Submitting to batch-sequence-full");
    return this.http.post<Sequence[]>(`${environment.apiUrl}/sequences/search`, query);
  }
}
