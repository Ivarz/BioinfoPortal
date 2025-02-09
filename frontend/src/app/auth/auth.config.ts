import { PassedInitialConfig } from 'angular-auth-oidc-client';
import { environment } from '../../environments/environment';

export const authConfig: PassedInitialConfig = {
  config: {
              authority: environment.authority, //for dev
              redirectUrl: window.location.origin,
              postLogoutRedirectUri: window.location.origin,
              clientId: 'spa',
              scope: 'openid profile', // 'openid profile offline_access ' + your scopes
              responseType: 'code',
              silentRenew: true,
              useRefreshToken: true,
              renewTimeBeforeTokenExpiresInSeconds: 30,
              secureRoutes: environment.secureRoutes,
          }
}
