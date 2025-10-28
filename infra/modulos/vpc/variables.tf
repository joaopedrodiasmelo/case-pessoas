variable "project_name" {
  description = "Nome do projeto, usado para nomear os recursos."
  type        = string
}

variable "vpc_cidr" {
  description = "O bloco CIDR principal para o VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Lista de blocos CIDR para as sub-redes públicas."
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "Lista de blocos CIDR para as sub-redes privadas."
  type        = list(string)
  default     = ["10.0.101.0/24", "10.0.102.0/24"]
}

variable "availability_zones" {
  description = "Lista de Zonas de Disponibilidade (AZs) para usar."
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}