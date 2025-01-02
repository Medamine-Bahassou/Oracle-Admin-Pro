export interface TableInfo {
  id?: number;
  tableName: string;
  columns: string[];
}

export interface TableRequest {
  tableName: string;
  columns: string[];
}
