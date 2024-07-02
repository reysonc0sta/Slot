export interface IDrink {
  id: number;
  nome?: string | null;
  marca?: string | null;
}

export type NewDrink = Omit<IDrink, 'id'> & { id: null };
