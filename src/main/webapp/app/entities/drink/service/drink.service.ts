import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDrink, NewDrink } from '../drink.model';

export type PartialUpdateDrink = Partial<IDrink> & Pick<IDrink, 'id'>;

export type EntityResponseType = HttpResponse<IDrink>;
export type EntityArrayResponseType = HttpResponse<IDrink[]>;

@Injectable({ providedIn: 'root' })
export class DrinkService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/drinks');

  create(drink: NewDrink): Observable<EntityResponseType> {
    return this.http.post<IDrink>(this.resourceUrl, drink, { observe: 'response' });
  }

  update(drink: IDrink): Observable<EntityResponseType> {
    return this.http.put<IDrink>(`${this.resourceUrl}/${this.getDrinkIdentifier(drink)}`, drink, { observe: 'response' });
  }

  partialUpdate(drink: PartialUpdateDrink): Observable<EntityResponseType> {
    return this.http.patch<IDrink>(`${this.resourceUrl}/${this.getDrinkIdentifier(drink)}`, drink, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDrink>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDrink[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDrinkIdentifier(drink: Pick<IDrink, 'id'>): number {
    return drink.id;
  }

  compareDrink(o1: Pick<IDrink, 'id'> | null, o2: Pick<IDrink, 'id'> | null): boolean {
    return o1 && o2 ? this.getDrinkIdentifier(o1) === this.getDrinkIdentifier(o2) : o1 === o2;
  }

  addDrinkToCollectionIfMissing<Type extends Pick<IDrink, 'id'>>(
    drinkCollection: Type[],
    ...drinksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const drinks: Type[] = drinksToCheck.filter(isPresent);
    if (drinks.length > 0) {
      const drinkCollectionIdentifiers = drinkCollection.map(drinkItem => this.getDrinkIdentifier(drinkItem));
      const drinksToAdd = drinks.filter(drinkItem => {
        const drinkIdentifier = this.getDrinkIdentifier(drinkItem);
        if (drinkCollectionIdentifiers.includes(drinkIdentifier)) {
          return false;
        }
        drinkCollectionIdentifiers.push(drinkIdentifier);
        return true;
      });
      return [...drinksToAdd, ...drinkCollection];
    }
    return drinkCollection;
  }
}
