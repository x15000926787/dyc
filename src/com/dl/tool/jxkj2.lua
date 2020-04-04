
local ai_0 = tonumber(redis.call('get', 'un_0_.ai_1_.value'))
local di_0 = tonumber(redis.call('get', 'un_0_.di_100_.value'))
if ((ai_0 >=20) and (di_0 == 1)) then
           redis.call('setex','un_0_.di_100_.value',60,'0');
 end
 if ((ai_0 < 20) and (di_0 == 0)) then
           redis.call('set','un_0_.di_100_.value','1');
      end



