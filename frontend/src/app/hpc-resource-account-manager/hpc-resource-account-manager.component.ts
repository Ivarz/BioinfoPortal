import { Component, inject, OnInit } from '@angular/core';
import { RoleService } from '../services/role.service';
import { HpcAccountService } from '../services/hpc-account.service';
import { RoleMappingService } from '../services/role-mapping.service';
import { HpcResourceAccount } from '../interfaces/hpc-resource-account.interface';
import { RoleMapping, RoleAccounts } from '../interfaces/role-mapping.interface';
import { MatTableModule } from '@angular/material/table';
import { FormControl, FormGroup, FormArray, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';

interface Dictionary<T> {
    [Key: string]: T;
}

@Component({
  selector: 'app-hpc-resource-account-manager',
  standalone: true,
  imports: [
    MatTableModule,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
  ],
  templateUrl: './hpc-resource-account-manager.component.html',
  styleUrl: './hpc-resource-account-manager.component.scss'
})
export class HpcResourceAccountManagerComponent implements OnInit {
  private roleService = inject(RoleService);
  private hpcAccountService = inject(HpcAccountService);
  private roleMappingService = inject(RoleMappingService);
  realmRoles: string[] = [];
  hpcAccounts: HpcResourceAccount[] = [];
  mappedAccounts = new FormGroup({});
  realmRoleMapping: RoleMapping[] = [];

  ngOnInit() {

    this.hpcAccountService.fetchAllHpcAccounts().subscribe(accounts => {
      this.hpcAccounts = accounts;
    });

    this.roleService.getRealmRoles().subscribe((roles: string[]) => {
      this.roleMappingService.findAllRoleAccounts().subscribe((roleAccounts: RoleAccounts[]) => {
        let roleDict: Dictionary<HpcResourceAccount[]> = {};
        this.realmRoles = roles;
        for (let roleAcc of roleAccounts) {
          roleDict[roleAcc.role] = roleAcc.resourceAccounts;
        }
        for (let role of roles) {
          if (roleDict[role] !== undefined) {
            this.mappedAccounts.addControl(role, new FormControl(roleDict[role]));
          } else {
            this.mappedAccounts.addControl(role, new FormControl([ ]));
          }
        }
      });
    });

    this.mappedAccounts.valueChanges.subscribe(_ => {
      this.onSubmitRoleMapping();
    });
  }
  compareFunction(v1: any, v2: any): boolean {
    return (v1.name === v2.name);
  }
  onSubmitRoleMapping() {
    let roleAccounts: RoleAccounts[] = [];
    for (let currRole in this.mappedAccounts.controls) {
      let currRoleAccounts = this.mappedAccounts.get(currRole)?.value;
      roleAccounts.push({'role': currRole, 'resourceAccounts': currRoleAccounts});
    }
    this.roleMappingService.setAllRoleAccounts(roleAccounts).subscribe();
  }
}
