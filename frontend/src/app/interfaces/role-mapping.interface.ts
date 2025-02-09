import { HpcResourceAccount } from './hpc-resource-account.interface'

export interface RoleMapping {
  id: number;
  role: string;
  resourceAccount: HpcResourceAccount;
}

export interface RoleAccounts {
  role: string;
  resourceAccounts: HpcResourceAccount[];
}
