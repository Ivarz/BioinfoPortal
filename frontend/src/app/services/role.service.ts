import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  private http = inject(HttpClient);
  getRealmRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiUrl}/admin/roles`);
  }
}
