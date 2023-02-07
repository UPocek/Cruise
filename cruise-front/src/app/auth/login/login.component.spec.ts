import { CommonModule } from "@angular/common";
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from "@angular/forms";
import { MaterialModule } from "../../material.module";
import {Router, RouterLink, RouterModule} from "@angular/router";
import {map, of} from "rxjs";
import { LoginDTO } from "../models/login-dto";
import { CredentialsDTO } from "../models/credentials-dto";
import { LoginService } from "../services/login.service";
import { LoginComponent } from './login.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {By} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AuthService} from "../services/auth.service";
import {PopUpService} from "../../universal-components/services/pop-up.service";

fdescribe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let email: string = 'mock@gmail.com'
  let password: string = 'mock'

  const credentials = <CredentialsDTO>{
    email: email,
    password: password
  }


  const loginDTO = <LoginDTO> {
    accessToken: 'mock',
    refreshToken: 'MOCK'
  }

  const loginServiceSpy = jasmine.createSpyObj<LoginService>([
    'loginUser', 'getUser'
  ]);
  const authServiceSpy = jasmine.createSpyObj<AuthService>([
    'getRole' , 'getId', 'setUser', 'getEmail'
  ]);

  authServiceSpy.getId.and.returnValue(1);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        RouterLink
      ],
      providers: [
        { provide: LoginService, useValue: loginServiceSpy },
        { provide: AuthService, useValue: authServiceSpy},
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.debugElement.componentInstance;
    window.localStorage.clear()
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display title', () => {
    const title = fixture.debugElement.query(By.css('.d-h1'))
      .nativeElement as HTMLElement;
    expect(title.textContent).toContain('LogIn')
  })

  it('form should be invalid for empty inputs', () => {
    component.loginForm.controls['email'].setValue('');
    component.loginForm.controls['password'].setValue('');
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should login passenger', () => {
    loginServiceSpy.loginUser.withArgs(credentials).and.returnValue(of(loginDTO))
    authServiceSpy.getRole.and.returnValue('ROLE_PASSENGER')
    loginServiceSpy.getUser.and.returnValue(of(false))


    spyOn(component, 'loginUser').and.callThrough();
    let router = fixture.debugElement.injector.get(Router);
    spyOn(router, 'navigateByUrl');
    fixture.detectChanges();



    component.loginForm.controls['email'].setValue(email);
    component.loginForm.controls['password'].setValue(password);

    const submitBtn = fixture.debugElement.query(
      By.css('#login-btn')
    ).nativeElement;
    submitBtn.click();

    expect(component.loginUser).toHaveBeenCalled();
    expect(component.loggedUser).toEqual(loginDTO);
    expect(router.navigateByUrl).toHaveBeenCalledOnceWith(
      '/passenger-main'
    );
  });

  it('should login driver', () => {
    loginServiceSpy.loginUser.withArgs(credentials).and.returnValue(of(loginDTO))
    authServiceSpy.getRole.and.returnValue('ROLE_DRIVER')
    loginServiceSpy.getUser.and.returnValue(of(false))
    spyOn(component, 'loginUser').and.callThrough();
    let router = fixture.debugElement.injector.get(Router);
    spyOn(router, 'navigateByUrl');
    fixture.detectChanges();


    component.loginForm.controls['email'].setValue(email);
    component.loginForm.controls['password'].setValue(password);

    const submitBtn = fixture.debugElement.query(
      By.css('#login-btn')
    ).nativeElement;
    submitBtn.click();

    expect(component.loginUser).toHaveBeenCalled();
    expect(component.loggedUser).toEqual(loginDTO);
    expect(router.navigateByUrl).toHaveBeenCalledOnceWith(
      '/driver-main'
    );
  });

  it('should login admin', () => {
    loginServiceSpy.loginUser.withArgs(credentials).and.returnValue(of(loginDTO))
    authServiceSpy.getRole.and.returnValue('ROLE_ADMIN')
    loginServiceSpy.getUser.and.returnValue(of(false))
    spyOn(component, 'loginUser').and.callThrough();
    let router = fixture.debugElement.injector.get(Router);
    spyOn(router, 'navigateByUrl');
    fixture.detectChanges();


    component.loginForm.controls['email'].setValue(email);
    component.loginForm.controls['password'].setValue(password);

    const submitBtn = fixture.debugElement.query(
      By.css('#login-btn')
    ).nativeElement;
    submitBtn.click();

    expect(component.loginUser).toHaveBeenCalled();
    expect(component.loggedUser).toEqual(loginDTO);
    expect(router.navigateByUrl).toHaveBeenCalledOnceWith(
      '/admin-main'
    );
  });

  it('should not login user is blocked', () => {
    loginServiceSpy.loginUser.withArgs(credentials).and.returnValue(of(loginDTO))
    authServiceSpy.getRole.and.returnValue('ROLE_PASSENGER')
    loginServiceSpy.getUser.and.returnValue(of(true))
    let popUpService = fixture.debugElement.injector.get(PopUpService);
    spyOn(popUpService, 'showPopUp');
    spyOn(component, 'loginUser').and.callThrough();
    let router = fixture.debugElement.injector.get(Router);
    spyOn(router, 'navigateByUrl');
    fixture.detectChanges();


    component.loginForm.controls['email'].setValue(email);
    component.loginForm.controls['password'].setValue(password);

    const submitBtn = fixture.debugElement.query(
      By.css('#login-btn')
    ).nativeElement;
    submitBtn.click();

    expect(component.loginUser).toHaveBeenCalled();
    expect(component.loggedUser).toEqual(loginDTO);
    expect(popUpService.showPopUp).toHaveBeenCalledOnceWith(
      'User is blocked'
    );
  });

  it('should not login user is null', () => {
    // @ts-ignore
    loginServiceSpy.loginUser.withArgs(credentials).and.returnValue(of( null).pipe(map(() => null)))
    let popUpService = fixture.debugElement.injector.get(PopUpService);
    spyOn(popUpService, 'showPopUp');
    spyOn(component, 'loginUser').and.callThrough();
    fixture.detectChanges();


    component.loginForm.controls['email'].setValue(email);
    component.loginForm.controls['password'].setValue(password);

    const submitBtn = fixture.debugElement.query(
      By.css('#login-btn')
    ).nativeElement;
    submitBtn.click();

    expect(component.loginUser).toHaveBeenCalled();
    expect(component.loggedUser).toEqual(null);
    expect(popUpService.showPopUp).toHaveBeenCalledOnceWith(
      'Login unsucessfull'
    );
  });



});
