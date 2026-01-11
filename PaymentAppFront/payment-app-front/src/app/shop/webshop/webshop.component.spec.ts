import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WebShopComponent } from './webshsop.component';

describe('WebshsopComponent', () => {
  let component: WebShopComponent;
  let fixture: ComponentFixture<WebShopComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WebShopComponent]
    });
    fixture = TestBed.createComponent(WebShopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
