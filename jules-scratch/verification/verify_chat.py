from playwright.sync_api import Page, expect
import re

def test_gemini_chat(page: Page):
    """
    This test verifies the real chat connection with the Gemini API.
    It sends a message and checks that a response from the agent appears.
    """
    # 1. Arrange: Go to the application's homepage.
    # The Ktor server runs on port 8080 by default.
    page.goto("http://localhost:8080/")

    # 2. Act: Find the chat input, type a message, and click send.
    chat_input = page.get_by_placeholder("Describe tu campaña o idea...")
    expect(chat_input).to_be_visible()
    chat_input.fill("Hello, this is a test. Please respond with a short confirmation.")

    send_button = page.get_by_role("button")
    expect(send_button).to_be_enabled()
    send_button.click()

    # 3. Assert: Wait for the agent's response to appear.
    # We'll look for the agent's name to confirm a response bubble has been added.
    # We'll use a longer timeout because the API call can be slow.
    agent_name_locator = page.locator(".agent-bubble .agent-name")
    expect(agent_name_locator).to_be_visible(timeout=30000)

    # Also verify that the "mission" is marked as complete.
    expect(page.get_by_text("✅ Completada")).to_be_visible()

    # 4. Screenshot: Capture the final result for visual verification.
    page.screenshot(path="jules-scratch/verification/verification.png")