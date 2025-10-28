variable "project_name" {
  description = "Nome do projeto, para nomear recursos."
  type        = string
}

variable "vpc_id" {
  description = "ID do VPC onde a EC2 será criada."
  type        = string
}

# variable "private_subnet_ids" {
#   description = "Lista de IDs das sub-redes privadas para a EC2."
#   type        = list(string)
# }

variable "public_subnet_ids" {
  description = "Lista de IDs das sub-redes PUBLICAS para a EC2."
  type        = list(string)
}

variable "app_secret_arn" {
  description = "ARN do segredo da aplicação (api-pessoas)."
  type        = string
}

variable "db_secret_arn" {
  description = "ARN do segredo do banco de dados (rds-credentials)."
  type        = string
}

variable "ami_id" {
  description = "ID da Amazon Machine Image (AMI) para usar. (Amazon Linux 2023)"
  type        = string
  default     = "ami-07860a2d7eb515d9a"
}
