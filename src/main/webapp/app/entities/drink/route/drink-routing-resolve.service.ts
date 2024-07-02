import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDrink } from '../drink.model';
import { DrinkService } from '../service/drink.service';

const drinkResolve = (route: ActivatedRouteSnapshot): Observable<null | IDrink> => {
  const id = route.params['id'];
  if (id) {
    return inject(DrinkService)
      .find(id)
      .pipe(
        mergeMap((drink: HttpResponse<IDrink>) => {
          if (drink.body) {
            return of(drink.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default drinkResolve;
