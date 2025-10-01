from playwright.sync_api import Page, expect

def test_chat_simulation(page: Page):
    """
    This test verifies that the Svelte frontend migration is working correctly.
    It simulates a user interacting with the chat and verifies that the UI updates as expected.
    """
    # 1. Arrange: Go to the application's homepage.
    # The dev server runs on port 5173 by default.
    page.goto("http://localhost:5173/")

    # 2. Act: Find the chat input, type a message, and click send.
    chat_input = page.get_by_placeholder("Describe tu campaña o idea...")
    expect(chat_input).to_be_visible()
    chat_input.fill("Hola, ¿puedes ayudarme a crear buyer personas para mi nueva campaña de marketing?")

    send_button = page.get_by_role("button", name="paper-plane-tilt")
    expect(send_button).to_be_enabled()
    send_button.click()

    # 3. Assert: Wait for the conversation to progress and the UI to update.
    # We'll wait for the final message from the user to appear.
    expect(page.get_by_text("Perfecto, esto es justo lo que necesitaba. Muy útil.")).to_be_visible(timeout=20000)

    # Also, verify that the canvas panel has been updated with the personas.
    expect(page.get_by_text("Carlos, el Emprendedor Tecnológico")).to_be_visible()
    expect(page.get_by_text("Ana, la Gerente de Marketing")).to_be_visible()

    # 4. Screenshot: Capture the final result for visual verification.
    page.screenshot(path="jules-scratch/verification/verification.png")