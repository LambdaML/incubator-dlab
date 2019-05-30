provider "google" {
  project     = "${var.project_var}"
  region = "${var.region}"
}

resource "google_compute_address" "ssn-ip" {
  name = "${var.project_name_var}-ssn-ip"
  address_type = "EXTERNAL"
}

resource "google_compute_instance" "ssn" {
  name = "${var.project_name_var}-ssn"
  machine_type         = "n1-standard-1"
  tags = ["${var.project_name_var}-ssn"]
  zone  = "${var.zone}"

  boot_disk {
    initialize_params {
      image = "${var.image_name}"
      size  = 20
    }
  }

  labels = {
    name = "${var.project_name_var}-ssn"
    product = "dlab"
    sbn = "${var.project_name_var}"
  }

  metadata = {
    ssh-keys = "ubuntu:${file("~/keys/id_rsa.pub")}"
  }

/*  service_account {
    email = "${var.project_name_var}-ssn-sa@${var.project_var}.iam.gserviceaccount.com"
    scopes = ["https://www.googleapis.com/auth/cloud-platform", "https://www.googleapis.com/auth/compute"]
  }
*/
  network_interface {
    network = "${var.project_name_var}-vpc"
    subnetwork = "${var.project_name_var}-subnet"
    access_config {
      nat_ip = "${google_compute_address.ssn-ip.address}"
    }
  }
}

