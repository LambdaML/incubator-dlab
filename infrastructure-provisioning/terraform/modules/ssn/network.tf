resource "google_compute_network" "vpc" {
  name          =  "${var.project_name_var}-vpc"
  auto_create_subnetworks = "false"
  routing_mode            = "GLOBAL"
}

resource "google_compute_subnetwork" "subnet" {
  name          = "${var.project_name_var}-subnet"
  ip_cidr_range = "172.31.0.0/20"
  region        = "us-east1"
  network       = "${google_compute_network.vpc.self_link}"
}

resource "google_compute_firewall" "firewall-ingress" {
  name    = "${var.project_name_var}-ssn-firewall-ingress"
  network = "${google_compute_network.vpc.name}"
  allow {
    protocol = "tcp"
    ports    = ["22", "80", "443"]
  }
  target_tags = ["${var.project_name_var}-ssn"]
  source_ranges = ["0.0.0.0/0"]
}

resource "google_compute_firewall" "firewall-egress" {
  name    = "${var.project_name_var}-ssn-firewall-egress"
  network = "${google_compute_network.vpc.name}"
  direction = "EGRESS"
  allow {
    protocol = "all"
  }
  target_tags = ["${var.project_name_var}-ssn"]
  destination_ranges = ["0.0.0.0/0"]
}
