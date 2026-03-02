function validateForm() {
  const contactInput = document.getElementById("contact").value;
  const contactError = document.getElementById("contact-error");

  // Regex for phone number (e.g. 123-456-7890 or 1234567890)
  const phoneRegex = /^(\d{3}[- ]?\d{3}[- ]?\d{4})$/;

  if (!phoneRegex.test(contactInput)) {
    contactError.classList.remove("d-none");
    return false;
  } else {
    contactError.classList.add("d-none");
  }

  const checkIn = document.getElementById("checkIn").value;
  const checkOut = document.getElementById("checkOut").value;

  if (checkIn && checkOut) {
    const d1 = new Date(checkIn);
    const d2 = new Date(checkOut);
    if (d2 <= d1) {
      alert("Check-out date must be after Check-in date.");
      return false;
    }
  }

  return true;
}
