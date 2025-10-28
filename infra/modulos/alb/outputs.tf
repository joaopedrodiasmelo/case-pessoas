output "alb_dns_name" {
  description = "O endereço DNS público do Load Balancer (URL da sua app)."
  value       = aws_lb.this.dns_name
}

output "alb_security_group_id" {
  description = "O ID do Security Group do ALB."
  value       = aws_security_group.this.id
}