import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { DrinkService } from '../service/drink.service';
import { IDrink } from '../drink.model';
import { DrinkFormService } from './drink-form.service';

import { DrinkUpdateComponent } from './drink-update.component';

describe('Drink Management Update Component', () => {
  let comp: DrinkUpdateComponent;
  let fixture: ComponentFixture<DrinkUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let drinkFormService: DrinkFormService;
  let drinkService: DrinkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DrinkUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DrinkUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DrinkUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    drinkFormService = TestBed.inject(DrinkFormService);
    drinkService = TestBed.inject(DrinkService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const drink: IDrink = { id: 456 };

      activatedRoute.data = of({ drink });
      comp.ngOnInit();

      expect(comp.drink).toEqual(drink);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDrink>>();
      const drink = { id: 123 };
      jest.spyOn(drinkFormService, 'getDrink').mockReturnValue(drink);
      jest.spyOn(drinkService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drink });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: drink }));
      saveSubject.complete();

      // THEN
      expect(drinkFormService.getDrink).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(drinkService.update).toHaveBeenCalledWith(expect.objectContaining(drink));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDrink>>();
      const drink = { id: 123 };
      jest.spyOn(drinkFormService, 'getDrink').mockReturnValue({ id: null });
      jest.spyOn(drinkService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drink: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: drink }));
      saveSubject.complete();

      // THEN
      expect(drinkFormService.getDrink).toHaveBeenCalled();
      expect(drinkService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDrink>>();
      const drink = { id: 123 };
      jest.spyOn(drinkService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drink });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(drinkService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
