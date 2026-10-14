import { Categoria } from './categoria.model';

export interface Producto {
  id?: number;
  idProducto?: number;
  codigo?: string;
  nombre: string;
  descripcion?: string;
  precio: number;
  stock: number;
  estado?: boolean;
  categoria?: Categoria;
  idCategoria?: number;
  nombreCategoria?: string;
  usernameRegistro?: string;
}

export interface ProductoRequest {
  codigo?: string;
  nombre: string;
  descripcion?: string;
  precio: number;
  stock: number;
  idCategoria?: number;
  categoriaId?: number;
}
