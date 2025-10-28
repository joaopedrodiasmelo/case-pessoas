variable "project_name" {
  description = "Nome do projeto, para nomear recursos."
  type        = string
}

variable "vpc_id" {
  description = "ID do VPC onde o banco de dados será criado."
  type        = string
}

variable "private_subnet_ids" {
  description = "Lista de IDs das sub-redes privadas para o RDS."
  type        = list(string)
}

variable "db_name" {
  description = "O nome do banco de dados inicial a ser criado."
  type        = string
}

variable "db_username" {
  description = "O nome do usuário mestre do banco."
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "A senha mestre para o banco de dados."
  type        = string
  sensitive   = true
}