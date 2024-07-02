import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../drink.test-samples';

import { DrinkFormService } from './drink-form.service';

describe('Drink Form Service', () => {
  let service: DrinkFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DrinkFormService);
  });

  describe('Service methods', () => {
    describe('createDrinkFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDrinkFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nome: expect.any(Object),
            marca: expect.any(Object),
          }),
        );
      });

      it('passing IDrink should create a new form with FormGroup', () => {
        const formGroup = service.createDrinkFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nome: expect.any(Object),
            marca: expect.any(Object),
          }),
        );
      });
    });

    describe('getDrink', () => {
      it('should return NewDrink for default Drink initial value', () => {
        const formGroup = service.createDrinkFormGroup(sampleWithNewData);

        const drink = service.getDrink(formGroup) as any;

        expect(drink).toMatchObject(sampleWithNewData);
      });

      it('should return NewDrink for empty Drink initial value', () => {
        const formGroup = service.createDrinkFormGroup();

        const drink = service.getDrink(formGroup) as any;

        expect(drink).toMatchObject({});
      });

      it('should return IDrink', () => {
        const formGroup = service.createDrinkFormGroup(sampleWithRequiredData);

        const drink = service.getDrink(formGroup) as any;

        expect(drink).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDrink should not enable id FormControl', () => {
        const formGroup = service.createDrinkFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDrink should disable id FormControl', () => {
        const formGroup = service.createDrinkFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
