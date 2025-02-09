import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, HTTP_INTERCEPTORS, withInterceptors } from '@angular/common/http';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { routes } from './app.routes';
import { authConfig } from './auth/auth.config';
import { provideAuth, AuthInterceptor, AuthModule, authInterceptor } from 'angular-auth-oidc-client';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAuth(authConfig),
    provideHttpClient(withInterceptors([authInterceptor()])),
    provideAnimationsAsync(),
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}, provideAnimationsAsync(), provideAuth(authConfig),
  ]
};
