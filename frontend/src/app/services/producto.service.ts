import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto, ProductoRequest } from '../models/producto.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiUrl = 'http://localhost:8080/api/productos';

  constructor(private http: HttpClient) {}

  listar(nombre?: string): Observable<Producto[]> {
    let params = new HttpParams();
    if (nombre && nombre.trim().length > 0) {
      params = params.set('nombre', nombre.trim());
    }
    return this.http.get<Producto[]>(this.apiUrl, { params });
  }

  obtener(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`);
  }

  crear(request: ProductoRequest): Observable<Producto> {
    return this.http.post<Producto>(this.apiUrl, request);
  }

  actualizar(id: number, request: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
