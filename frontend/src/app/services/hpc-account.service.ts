import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HpcAccountService {

  private http = inject(HttpClient);
  private readonly oidcSecurityService = inject(OidcSecurityService);

  constructor() { }
  fetchHpcAccounts(): Observable<any> {
    return this.http.get(`${environment.apiUrl}/hpc/resource-accounts`);
  }
  fetchAllHpcAccounts(): Observable<any> {
    return this.http.get(`${environment.apiUrl}/admin/resource-accounts`);
  }
  updateHpcAccounts() {
    this.http.get(`${environment.apiUrl}/hpc/resource-accounts`).subscribe((sub) => {
            console.log(sub);
    });
  }
}
