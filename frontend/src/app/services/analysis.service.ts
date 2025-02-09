import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalysisRequest, AnalysisResultObjectDTO } from '../interfaces/analysis.interface';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class AnalysisService {

  private http = inject(HttpClient);

  currentUserAnalysisRequests(): Observable<AnalysisRequest[]> {
    return this.http.get<AnalysisRequest[]>(`${environment.apiUrl}/analysis-requests`);
  }

  submitAnalysisRequest(submissionInfo: any): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(`${environment.apiUrl}/analysis-requests`, submissionInfo, { headers });
  }
  resumeAnalysisRequest(aReq: AnalysisRequest) {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<AnalysisRequest>(`${environment.apiUrl}/analysis-requests`, aReq, { headers });
  }
  downloadAnalysisResults(aReqObjDTO: AnalysisResultObjectDTO): Observable<Blob> {
    return this.http.get(`${environment.apiUrl}/results-download?analysisId=${aReqObjDTO.analysisId}&fileName=${aReqObjDTO.fileName}`, {
      responseType: 'blob'
    });
  }
  listAnalysisResults(aReq: AnalysisRequest): Observable<AnalysisResultObjectDTO[]> {
    //e862ce76-fb80-44f6-9341-dca0f6e565c6 d95c2a8f-c0c1-458f-a93a-4f3f2857c047
    return this.http.get<AnalysisResultObjectDTO[]>(`${environment.apiUrl}/list-results?analysisId=${aReq.id}`);
  }
  deleteAnalysisResults(aReq: AnalysisRequest) {
    //e862ce76-fb80-44f6-9341-dca0f6e565c6 d95c2a8f-c0c1-458f-a93a-4f3f2857c047
    return this.http.delete(`${environment.apiUrl}/analysis-results?analysisId=${aReq.id}`);
  }
  sendDummyAnalysisDeleteRequest() {
    let dummyValue = "1f39de84-b32d-4c94-a62e-7feef29a9d6e";
    return this.http.delete(`${environment.apiUrl}/analysis-results?analysisId=${dummyValue}`);
  }
}
