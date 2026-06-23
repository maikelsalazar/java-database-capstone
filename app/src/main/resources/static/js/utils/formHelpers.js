function clearMessages() {
  const globalMessage = document.getElementById("globalErrorMessage");

  if (globalMessage) {
    globalMessage.textContent = "";
  }

  document.querySelectorAll("[id$='Message']").forEach(elem => {
      elem.textContent = "";
  });
}

/**
 * response expects to be an object like
 * {
 *   success: boolean,
 *   message: string,
 *   errors {
 *       field1: string,
 *       field2: string,
 *   }
 * }
 */
function showMessages(response) {
  if (!response || response.success) return;

  if (response.message) {
    showFieldMessage("globalErrorMessage", response.message);
  }

  Object.entries(response.errors || {}).forEach(([field, message]) => {
    showFieldMessage(`${field}Message`, message);
  });
}

function showFieldMessage(fieldId, message) {
  const field = document.getElementById(fieldId);
  if (field) {
    field.textContent = message;
  }
}

export {
  clearMessages,
  showMessages,
  showFieldMessage
}
