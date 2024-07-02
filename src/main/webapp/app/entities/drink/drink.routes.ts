import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { DrinkComponent } from './list/drink.component';
import { DrinkDetailComponent } from './detail/drink-detail.component';
import { DrinkUpdateComponent } from './update/drink-update.component';
import DrinkResolve from './route/drink-routing-resolve.service';

const drinkRoute: Routes = [
  {
    path: '',
    component: DrinkComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DrinkDetailComponent,
    resolve: {
      drink: DrinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DrinkUpdateComponent,
    resolve: {
      drink: DrinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DrinkUpdateComponent,
    resolve: {
      drink: DrinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default drinkRoute;
