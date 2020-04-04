
local ai_0 = tonumber(redis.call('get', 'un_0_.ai_0_.value'))
local ai_1 = tonumber(redis.call('get', 'un_0_.ai_1_.value'))
if ai_0 > 100 then
           redis.call('set','un_0_.di_99_.value','1');
 else
           redis.call('set','un_0_.di_99_.value','0');
      end



