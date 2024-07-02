import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDrink, NewDrink } from '../drink.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDrink for edit and NewDrinkFormGroupInput for create.
 */
type DrinkFormGroupInput = IDrink | PartialWithRequiredKeyOf<NewDrink>;

type DrinkFormDefaults = Pick<NewDrink, 'id'>;

type DrinkFormGroupContent = {
  id: FormControl<IDrink['id'] | NewDrink['id']>;
  nome: FormControl<IDrink['nome']>;
  marca: FormControl<IDrink['marca']>;
};

export type DrinkFormGroup = FormGroup<DrinkFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DrinkFormService {
  createDrinkFormGroup(drink: DrinkFormGroupInput = { id: null }): DrinkFormGroup {
    const drinkRawValue = {
      ...this.getFormDefaults(),
      ...drink,
    };
    return new FormGroup<DrinkFormGroupContent>({
      id: new FormControl(
        { value: drinkRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nome: new FormControl(drinkRawValue.nome, {
        validators: [Validators.required],
      }),
      marca: new FormControl(drinkRawValue.marca),
    });
  }

  getDrink(form: DrinkFormGroup): IDrink | NewDrink {
    return form.getRawValue() as IDrink | NewDrink;
  }

  resetForm(form: DrinkFormGroup, drink: DrinkFormGroupInput): void {
    const drinkRawValue = { ...this.getFormDefaults(), ...drink };
    form.reset(
      {
        ...drinkRawValue,
        id: { value: drinkRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DrinkFormDefaults {
    return {
      id: null,
    };
  }
}
