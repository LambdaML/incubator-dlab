variable "project" {
  default = "service_base_name"
}

variable "region" {
  default = "us-east1"
}

variable "credentials" {
  default = "/path/to/service_account.json"
}

variable "ssn_roles" {
  default = "/path/to/ssn_roles.json"
}

variable "ssn_police" {
  default = "/path/to/ssn_policy.json"
}