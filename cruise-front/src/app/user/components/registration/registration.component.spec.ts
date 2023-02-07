import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { By } from '@angular/platform-browser';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/material.module';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { UniversalComponentsModule } from 'src/app/universal-components/universal-components.module';
import { RegisteredUserDTO } from '../../models/registered-user-dto';
import { RegistrationService } from '../../services/registration.service';
import { RegistrationComponent } from './registration.component';

fdescribe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;
  const registeredUser = <RegisteredUserDTO>{
    id: 1,
    name: 'Mock',
    surname: 'Mockovic',
    email: 'mock@gmail.com',
    address: 'Mock adresa',
    profilePicture: 'data:image/jpeg;base64, ',
    telephoneNumber: '0238802388',
  };

  const registrationServiceSpy = jasmine.createSpyObj<RegistrationService>([
    'registerUser',
  ]);

  registrationServiceSpy.registerUser.and.returnValue(of(registeredUser));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegistrationComponent],
      imports: [
        CommonModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        FormsModule,
        MatInputModule,
        MatIconModule,
        RouterLink,
        RouterModule,
        MatButtonModule,
        UniversalComponentsModule,
        MatSelectModule,
        MatCheckboxModule,
        MaterialModule,
      ],
      providers: [
        { provide: RegistrationService, useValue: registrationServiceSpy },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistrationComponent);
    component = fixture.debugElement.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display title', () => {
    const title = fixture.debugElement.query(By.css('.d-h1'))
      .nativeElement as HTMLElement;
    expect(title.textContent).toContain('Registration');
  });

  it('form should be invalid for empty inputs', () => {
    component.registrationForm.controls['email'].setValue('');
    component.registrationForm.controls['name'].setValue('');
    component.registrationForm.controls['surname'].setValue('');
    component.registrationForm.controls['phone'].setValue('');
    component.registrationForm.controls['address'].setValue('');
    component.registrationForm.controls['password'].setValue('');
    component.registrationForm.controls['repassword'].setValue('');
    expect(component.registrationForm.valid).toBeFalsy();
  });

  it('form should be invalid for not matching phone pattern', () => {
    component.registrationForm.controls['email'].setValue('mock@gmail.com');
    component.registrationForm.controls['name'].setValue('Mock');
    component.registrationForm.controls['surname'].setValue('Mockovic');
    component.registrationForm.controls['phone'].setValue('0238802388blabla');
    component.registrationForm.controls['address'].setValue('Mock adresa');
    component.registrationForm.controls['password'].setValue('mock');
    component.registrationForm.controls['repassword'].setValue('mock');
    expect(component.registrationForm.valid).toBeFalsy();
  });

  it('form should be invalid for too short name', () => {
    component.registrationForm.controls['email'].setValue('mock@gmail.com');
    component.registrationForm.controls['name'].setValue('M');
    component.registrationForm.controls['surname'].setValue('Mockovic');
    component.registrationForm.controls['phone'].setValue('0238802388');
    component.registrationForm.controls['address'].setValue('Mock adresa');
    component.registrationForm.controls['password'].setValue('mock');
    component.registrationForm.controls['repassword'].setValue('mock');
    expect(component.registrationForm.valid).toBeFalsy();
  });

  it('form should be invalid for not matching passwords', () => {
    let popUpService = fixture.debugElement.injector.get(PopUpService);
    spyOn(popUpService, 'showPopUp');
    spyOn(component, 'registerUser').and.callThrough();

    fixture.detectChanges();

    component.registrationForm.controls['email'].setValue('mock@gmail.com');
    component.registrationForm.controls['name'].setValue('Mock');
    component.registrationForm.controls['surname'].setValue('Mockovic');
    component.registrationForm.controls['phone'].setValue('0238802388');
    component.registrationForm.controls['address'].setValue('Mock adresa');
    component.registrationForm.controls['password'].setValue('bla bla');
    component.registrationForm.controls['repassword'].setValue('mock');

    const submitBtn = fixture.debugElement.query(
      By.css('#registration-btn')
    ).nativeElement;
    submitBtn.click();

    expect(component.registerUser).toHaveBeenCalled();
    expect(popUpService.showPopUp).toHaveBeenCalledOnceWith(
      'Registration unsucessfull! Passwords do not match'
    );
  });

  it('should register user', () => {
    spyOn(component, 'registerUser').and.callThrough();
    let router = fixture.debugElement.injector.get(Router);
    spyOn(router, 'navigateByUrl');
    fixture.detectChanges();

    component.registrationForm.controls['email'].setValue('mock@gmail.com');
    component.registrationForm.controls['name'].setValue('Mock');
    component.registrationForm.controls['surname'].setValue('Mockovic');
    component.registrationForm.controls['phone'].setValue('0238802388');
    component.registrationForm.controls['address'].setValue('Mock adresa');
    component.registrationForm.controls['password'].setValue('mock');
    component.registrationForm.controls['repassword'].setValue('mock');

    const submitBtn = fixture.debugElement.query(
      By.css('#registration-btn')
    ).nativeElement;
    submitBtn.click();

    expect(component.registerUser).toHaveBeenCalled();
    expect(component.form_submited).toBe(true);
    expect(component.registeredUserDTO).toEqual(registeredUser);
    expect(router.navigateByUrl).toHaveBeenCalledOnceWith(
      '/verification-pending'
    );
  });
});
