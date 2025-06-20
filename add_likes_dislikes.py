import json

with open("spotify_1000_tracks_20250618_153243.json", "r", encoding="utf-8") as f:
    data = json.load(f)

for track in data["tracks"]:
    track.setdefault("likes", 0)
    track.setdefault("dislikes", 0)

with open("spotify_1000_tracks_20250618_153243.json", "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)