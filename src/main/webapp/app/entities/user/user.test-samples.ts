import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 21115,
  login: 'UA7P@JM\\]Ow\\)X-SAy\\_PcQT\\^0\\93z3',
};

export const sampleWithPartialData: IUser = {
  id: 2236,
  login: 'iDsT',
};

export const sampleWithFullData: IUser = {
  id: 27446,
  login: 'AwR@IG\\[tn\\+gnS\\8CHKdJf\\zDV\\3n6OoSR',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
