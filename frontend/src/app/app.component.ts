import { Component, OnInit, inject, computed } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    MatButtonModule,
    MatSidenavModule,
    MatDividerModule,
    MatListModule,
    MatIconModule,
    MatToolbarModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'BioSeq';
  private readonly oidcSecurityService = inject(OidcSecurityService);
  loggedIn = false;
  isAdmin = false;
  userData = this.oidcSecurityService.userData;
  private matIconReg = inject(MatIconRegistry);

  ngOnInit(): void {
    this.oidcSecurityService
      .checkAuth()
      .subscribe(({isAuthenticated, accessToken}) => {
        this.loggedIn = isAuthenticated;
        if (!isAuthenticated) {
          this.login();
        } else {
          this.oidcSecurityService.getPayloadFromAccessToken().subscribe(payload => {
            if (Object.hasOwn(payload, 'resource_access') && Object.hasOwn(payload['resource_access'], 'spa')) {
              this.isAdmin = payload['resource_access']['spa']['roles'].includes('portal-admin');
            }
          });
        }
      });
    this.matIconReg.setDefaultFontSetClass('material-symbols-outlined');
  }
  login(): void {
    this.oidcSecurityService.authorize();
  }
  logout(): void {
    this.oidcSecurityService
      .logoff()
      .subscribe((result) => console.log(result));
  }

}
