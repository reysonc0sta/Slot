import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDrink } from '../drink.model';
import { DrinkService } from '../service/drink.service';
import { DrinkFormService, DrinkFormGroup } from './drink-form.service';

@Component({
  standalone: true,
  selector: 'jhi-drink-update',
  templateUrl: './drink-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DrinkUpdateComponent implements OnInit {
  isSaving = false;
  drink: IDrink | null = null;

  protected drinkService = inject(DrinkService);
  protected drinkFormService = inject(DrinkFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DrinkFormGroup = this.drinkFormService.createDrinkFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ drink }) => {
      this.drink = drink;
      if (drink) {
        this.updateForm(drink);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const drink = this.drinkFormService.getDrink(this.editForm);
    if (drink.id !== null) {
      this.subscribeToSaveResponse(this.drinkService.update(drink));
    } else {
      this.subscribeToSaveResponse(this.drinkService.create(drink));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDrink>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(drink: IDrink): void {
    this.drink = drink;
    this.drinkFormService.resetForm(this.editForm, drink);
  }
}
