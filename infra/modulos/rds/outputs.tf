output "db_instance_endpoint" {
  description = "O hostname (endpoint) do banco de dados."
  value       = aws_db_instance.this.endpoint
}

output "db_instance_port" {
  description = "A porta do banco de dados."
  value       = aws_db_instance.this.port
}

output "db_security_group_id" {
  description = "O ID do Security Group do banco de dados."
  value       = aws_security_group.this.id
}

output "db_name" {
  description = "O nome do banco de dados."
  value       = aws_db_instance.this.db_name
}

output "db_username" {
  description = "O nome do usuário mestre."
  value       = aws_db_instance.this.username
}

output "db_password" {
  description = "A senha mestre (sensível)."
  value       = var.db_password
  sensitive   = true
}

output "db_instance_address" {
  description = "O endereco IP privado do banco de dados."
  value       = aws_db_instance.this.address
}