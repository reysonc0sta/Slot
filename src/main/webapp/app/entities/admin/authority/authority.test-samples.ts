import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '3ad2c97c-e0ae-4dbf-a0d7-345574e62618',
};

export const sampleWithPartialData: IAuthority = {
  name: 'ab2c9beb-5a27-4f69-b86f-8a72a2a86f04',
};

export const sampleWithFullData: IAuthority = {
  name: '7e3ea935-1703-4e06-bf20-b3e22c0cad26',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
