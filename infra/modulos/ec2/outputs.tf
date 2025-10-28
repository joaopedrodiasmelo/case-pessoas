output "instance_id" {
  description = "O ID da instância EC2 criada."
  value       = aws_instance.this.id
}

output "app_security_group_id" {
  description = "O ID do Security Group da aplicação."
  value       = aws_security_group.this.id
}

output "public_ip" {
  description = "O IP publico da instancia EC2."
  value       = aws_instance.this.public_ip
}