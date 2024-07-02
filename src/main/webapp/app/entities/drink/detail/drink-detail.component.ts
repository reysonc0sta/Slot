import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IDrink } from '../drink.model';

@Component({
  standalone: true,
  selector: 'jhi-drink-detail',
  templateUrl: './drink-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class DrinkDetailComponent {
  drink = input<IDrink | null>(null);

  previousState(): void {
    window.history.back();
  }
}
