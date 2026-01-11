export type CarType = 'SEDAN' | 'SUV' | 'CABRIO' | 'VAN'

export interface Car {
  id: number;
  mark: string;
  model: string;
  engineSize: number;
  doorNumber: number;
  type: CarType;
  year: number;
  rentPrice: number;
  picture:string;
}