import { writable } from 'svelte/store';

export const messages = writable([]);

export const addMessage = (sender, content) => {
    messages.update(currentMessages => [...currentMessages, { sender, content, id: Date.now() }]);
};

export const showTypingIndicator = writable(false);