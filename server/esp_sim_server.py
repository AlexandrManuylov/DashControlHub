import asyncio
import websockets
import json
import threading
import keyboard  # pip install keyboard

connected_clients = set()

commands = {
    "1": "volume_up",
    "2": "volume_down",
    "3": "next_track",
    "4": "prev_track",
    "5": "accept_call",
    "6": "reject_call"
}

async def send_command(cmd):
    payload = {
            "commandType": "media"
        }
    payload["message"] = cmd

    msg = json.dumps(payload)
    print(f"Sending: {msg}")

    if connected_clients:
        await asyncio.gather(*[client.send(msg) for client in connected_clients])
    else:
        print("No clients connected.")

async def handler(websocket):
    print("Client connected")
    connected_clients.add(websocket)

    try:
        async for message in websocket:
            print(f"Received: {message}")
    except websockets.ConnectionClosed:
        print("Client disconnected")
    finally:
        connected_clients.remove(websocket)

async def main():
    print("ESP Sim Server running on port 12346...")
    server = await websockets.serve(handler, "0.0.0.0", 12346)

    loop = asyncio.get_running_loop()

    def keyboard_loop():
        print("Ready! Press keys 1-6 to send commands.")
        while True:
            event = keyboard.read_event()
            if event.event_type == keyboard.KEY_DOWN:
                key = event.name
                cmd = commands.get(key)
                if cmd:
                    asyncio.run_coroutine_threadsafe(send_command(cmd), loop)

    threading.Thread(target=keyboard_loop, daemon=True).start()

    await asyncio.Future()  # run forever

if __name__ == "__main__":
    asyncio.run(main())