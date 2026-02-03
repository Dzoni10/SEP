-- Uklanja stari UNIQUE constraint na web_shop_id koji je ostao
-- iz prethodne verzije (dopuštao samo jednu transakciju po web shopu)
-- Sada imamo composite UNIQUE na (web_shop_id, order_id)
ALTER TABLE transaction DROP CONSTRAINT IF EXISTS ukq16632nbvwahbcv07lv8e2jwf;
