#!/bin/bash
FONT="/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
OUT="feature_graphic_1024x500.png"

# 1. Base gradient canvas (1024x500)
convert -size 1024x500 gradient:"#070B19"-"#131E3A" base.png

# 2. Resize icon to 180x180
convert playstore_icon_512.png -resize 180x180! icon_180.png

# 3. Create composite with icon, decorative grid tiles, and typography
convert base.png \
  \( icon_180.png -background none -shadow 80x8+0+4 \) -geometry +422+45 -composite \
  icon_180.png -geometry +422+45 -composite \
  -font "$FONT" \
  -fill "#000000" -pointsize 52 -gravity center -annotate +2+62 "WORD HUNTER QUEST" \
  -fill "#F59E0B" -pointsize 52 -gravity center -annotate +0+60 "WORD HUNTER QUEST" \
  -fill "#FDE68A" -pointsize 52 -gravity center -annotate +0+58 "WORD HUNTER QUEST" \
  -fill "#38BDF8" -pointsize 20 -gravity center -annotate +0+120 "★ 20 THEMED LEVELS • 8-WAY WORD SEARCH • CLOUD SYNC ★" \
  -fill "#94A3B8" -pointsize 15 -gravity center -annotate +0+165 "OFFLINE PLAY • PERSISTENT SCORES • HINTS" \
  -stroke "#F59E0B" -strokewidth 5 -fill none -draw "roundrectangle 20,20 1004,480 24,24" \
  -stroke "#0284C7" -strokewidth 2 -fill none -draw "roundrectangle 28,28 996,472 18,18" \
  "$OUT"

# Also copy to res/drawable for app project tracking
cp "$OUT" app/src/main/res/drawable/feature_graphic_1024x500.png
rm -f base.png icon_180.png
ls -lh "$OUT"
