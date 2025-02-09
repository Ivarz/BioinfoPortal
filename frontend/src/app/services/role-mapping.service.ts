import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { HpcResourceAccount } from '../interfaces/hpc-resource-account.interface'
import { RoleMapping, RoleAccounts } from '../interfaces/role-mapping.interface'
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RoleMappingService {

  private http = inject(HttpClient);
  findAllRoleAccounts(): Observable<RoleAccounts[]> {
    return this.http.get<RoleAccounts[]>(`${environment.apiUrl}/admin/role-accounts`);
  }
  setAllRoleAccounts(acc: RoleAccounts[]): Observable<any> {
    return this.http.post(`${environment.apiUrl}/admin/role-accounts`, acc);
  }
}
