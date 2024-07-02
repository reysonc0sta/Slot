import { IDrink, NewDrink } from './drink.model';

export const sampleWithRequiredData: IDrink = {
  id: 31286,
  nome: 'concerning thoughtfully bidding',
};

export const sampleWithPartialData: IDrink = {
  id: 9629,
  nome: 'intensely likewise unnecessarily',
  marca: 'now publish toil',
};

export const sampleWithFullData: IDrink = {
  id: 12604,
  nome: 'aw amongst ha',
  marca: 'pish greedily',
};

export const sampleWithNewData: NewDrink = {
  nome: 'upliftingly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
