import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SequenceDbMaintenance } from '../interfaces/sequence.interface';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class SequenceDbMaintenanceService {

  private http = inject(HttpClient);

  maintenanceState(): Observable<SequenceDbMaintenance> {
    return this.http.get<SequenceDbMaintenance>(`${environment.apiUrl}/sequences/maintenance`);
  }

}
