import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sequence } from './sequence.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class UploadService {

  constructor(private http: HttpClient) { }

  uploadSampleRequest(sampleRequests: Sequence[]): Observable<any> {
    return this.http.post<Sequence[]>(`${environment.apiUrl}/sequences/validate`, sampleRequests);
  }
}
