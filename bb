-- ============================================================================
-- BYPASS / ANTI-DETECTION MODULE (fully pcall-wrapped, optional)
-- ============================================================================
local Bypass = pcall(function()
    local Bypass = {}
    local game = game
    local pcall = pcall
    local coroutine = coroutine
    local getrawmetatable = getrawmetatable
    local setreadonly = setreadonly
    local hookfunction = hookfunction
    local newcclosure = newcclosure
    local checkcaller = checkcaller
    local getgenv = getgenv or function() return _G end
    local getsenv = getsenv or function() return nil end
    local getupvalues = getupvalues or function() return {} end
    local setupvalue = setupvalue or function() end
    
    Bypass.__active = true
    
    -- Only perform safe, non-breaking stealth measures
    local function stealthLogDisable()
        local oldPrint = print
        print = function(...)
            local msg = table.concat({...}, " ")
            if msg and (msg:match("Lyra") or msg:match("Bypass") or msg:match("inject") or msg:match("detect")) then
                return
            end
            oldPrint(...)
        end
        local oldWarn = warn
        warn = function(...)
            local msg = table.concat({...}, " ")
            if msg and (msg:match("Lyra") or msg:match("Bypass") or msg:match("inject") or msg:match("detect")) then
                return
            end
            oldWarn(...)
        end
    end
    pcall(stealthLogDisable)
    
    -- Spoof inputs with tiny random delays
    local userInput = game:GetService("UserInputService")
    if userInput then
        local oldInputBegan = userInput.InputBegan
        if oldInputBegan then
            userInput.InputBegan = function(self, ...)
                if Bypass.__active then
                    task.wait(math.random(1, 5) / 1000)
                end
                return oldInputBegan(self, ...)
            end
        end
    end
    
    -- Periodic maintenance loop (only safe re-runs)
    coroutine.wrap(function()
        while Bypass.__active do
            task.wait(15)
            pcall(stealthLogDisable)
        end
    end)()
    
    -- Identity rotation (safe)
    coroutine.wrap(function()
        while Bypass.__active do
            task.wait(45)
            pcall(setthreadidentity, math.random(2, 5))
        end
    end)()
    
    getgenv().Bypass = Bypass
    return Bypass
end)
-- If Bypass failed to load, that's fine - all features still work
if not Bypass then
    Bypass = { __active = false }
    getgenv().Bypass = Bypass
end
-- ============================================================================
-- END BYPASS MODULE
-- ============================================================================

-- Safety check for cloneref
local cloneref = cloneref or function(obj) return obj end

-- ============================================================================

-- ============================================================================
-- LYRA BLADE BALL: STRICT MOBILE BUILD
-- ============================================================================
pcall(function()
    game:GetService("StarterGui"):SetCore("SendNotification", {
        Title = "Blade Ball Lyra",
        Text = "Strict Mobile Build Active (Combined Touch/Tap Mode Loaded)",
        Duration = 5
    })
end)
-- ============================================================================

if _G.BladeBallLyraMerged then
    return warn('Already loaded.')
end
_G.BladeBallLyraMerged = true
local getgenv = getgenv or function() return _G end
notify = notify or function(title, text)
    warn(("[LyraBladeBall] %s - %s"):format(title or "Notice", text or ""))
end

local VERSION = "4.0-Lyra"

local Players           = cloneref(game:GetService('Players'))
local ReplicatedStorage = cloneref(game:GetService('ReplicatedStorage'))
local UserInputService  = cloneref(game:GetService('UserInputService'))
local RunService        = cloneref(game:GetService('RunService'))
local TweenService      = cloneref(game:GetService('TweenService'))
local Stats             = cloneref(game:GetService('Stats'))
local Debris            = cloneref(game:GetService('Debris'))
local CoreGui           = cloneref(game:GetService('CoreGui'))
local HttpService       = cloneref(game:GetService('HttpService'))
local Workspace         = cloneref(game:GetService('Workspace'))
local VIM               = cloneref(game:GetService("VirtualInputManager"))
local Lighting          = cloneref(game:GetService('Lighting'))
local ContentProvider   = cloneref(game:GetService('ContentProvider'))
local TextService       = cloneref(game:GetService('TextService'))

local LocalPlayer = Players.LocalPlayer
local Camera = Workspace.CurrentCamera or Workspace:WaitForChild("Camera", 10)

if not LocalPlayer.Character then
    LocalPlayer.CharacterAdded:Wait()
end

local Alive = Workspace:FindFirstChild("Alive") or Workspace:WaitForChild("Alive")
local Runtime = Workspace:FindFirstChild("Runtime") or Workspace:WaitForChild("Runtime")


local function safePing()
    local ok, ping = pcall(function()
        return Stats.Network.ServerStatsItem["Data Ping"]:GetValue()
    end)
    return ok and math.floor((ping or 0) + 0.5) or 0
end

getgenv().AutoParryMode = getgenv().AutoParryMode or "Combined (Mobile)"
getgenv().HoldSpamEnabled = getgenv().HoldSpamEnabled or false
getgenv().HoldSpamKey = getgenv().HoldSpamKey or "V"
getgenv().CooldownProtection = getgenv().CooldownProtection or false
getgenv().AutoAbility = getgenv().AutoAbility or false
getgenv().AutoStopEmote = getgenv().AutoStopEmote ~= false
getgenv().PendingAvatarTarget = getgenv().PendingAvatarTarget or ""
getgenv().swordModel = getgenv().swordModel or ""
getgenv().swordAnimations = getgenv().swordAnimations or ""
getgenv().swordFX = getgenv().swordFX or ""
getgenv().skinChangerEnabled = getgenv().skinChangerEnabled or false
getgenv().changeSwordModel = getgenv().changeSwordModel or false
getgenv().changeSwordAnimation = getgenv().changeSwordAnimation or false
getgenv().changeSwordFX = getgenv().changeSwordFX or false

getgenv().GG = {
    Language = {
        CheckboxEnabled = "Enabled",
        CheckboxDisabled = "Disabled",
        SliderValue = "Value",
        DropdownSelect = "Select",
        DropdownNone = "None",
        DropdownSelected = "Selected",
        ButtonClick = "Click",
        TextboxEnter = "Enter",
        ModuleEnabled = "Enabled",
        ModuleDisabled = "Disabled",
        TabGeneral = "General",
        TabSettings = "Settings",
        Loading = "Loading...",
        Error = "Error",
        Success = "Success"
    }
}

function convertStringToTable(inputString)
    local result = {}
    for value in string.gmatch(inputString, "([^,]+)") do
        local trimmedValue = value:match("^%s*(.-)%s*$")
        table.insert(result, trimmedValue)
    end
    return result
end

function convertTableToString(inputTable)
    return table.concat(inputTable, ", ")
end

local mouse = LocalPlayer:GetMouse()
local old_UI = CoreGui:FindFirstChild('BladeBallUI')

if old_UI then
    Debris:AddItem(old_UI, 0)
end

-- Per-script config root (never shared with other Lyra scripts)
local LYRA_SCRIPT_ID = "Blade Ball"
local LYRA_ROOT = "Lyra/" .. LYRA_SCRIPT_ID
local LYRA_CONFIG_FOLDER = LYRA_ROOT .. "/Configs"
local LYRA_AUTOLOAD_FILE = LYRA_ROOT .. "/Autoload.txt"
pcall(function()
    if isfolder and makefolder then
        if not isfolder("Lyra") then makefolder("Lyra") end
        if not isfolder(LYRA_ROOT) then makefolder(LYRA_ROOT) end
        if not isfolder(LYRA_CONFIG_FOLDER) then makefolder(LYRA_CONFIG_FOLDER) end
    end
end)

local Connections = setmetatable({
    disconnect = function(self, connection)
        if not self[connection] then
            return
        end
        self[connection]:Disconnect()
        self[connection] = nil
    end,
    disconnect_all = function(self)
        for _, value in pairs(self) do
            if typeof(value) ~= 'function' then
                value:Disconnect()
            end
        end
    end
}, Connections)

local Util = setmetatable({
    map = function(self, value, in_minimum, in_maximum, out_minimum, out_maximum)
        return (value - in_minimum) * (out_maximum - out_minimum) / (in_maximum - in_minimum) + out_minimum
    end,
    viewport_point_to_world = function(self, location, distance)
        local unit_ray = Workspace.CurrentCamera:ScreenPointToRay(location.X, location.Y)
        return unit_ray.Origin + unit_ray.Direction * distance
    end,
    get_offset = function(self)
        local viewport_size_Y = Workspace.CurrentCamera.ViewportSize.Y
        return self:map(viewport_size_Y, 0, 2560, 8, 56)
    end
}, Util)

local AcrylicBlur = {}
AcrylicBlur.__index = AcrylicBlur

function AcrylicBlur.new(object)
    local self = setmetatable({
        _object = object,
        _folder = nil,
        _frame = nil,
        _root = nil
    }, AcrylicBlur)
    self:setup()
    return self
end

function AcrylicBlur:create_folder()
    local old_folder = Workspace.CurrentCamera:FindFirstChild('AcrylicBlur')
    if old_folder then
        Debris:AddItem(old_folder, 0)
    end
    local folder = Instance.new('Folder')
    folder.Name = 'AcrylicBlur'
    folder.Parent = Workspace.CurrentCamera
    self._folder = folder
end

function AcrylicBlur:create_depth_of_fields()
    local depth_of_fields = Lighting:FindFirstChild('AcrylicBlur') or Instance.new('DepthOfFieldEffect')
    depth_of_fields.FarIntensity = 0
    depth_of_fields.FocusDistance = 0.05
    depth_of_fields.InFocusRadius = 0.1
    depth_of_fields.NearIntensity = 1
    depth_of_fields.Name = 'AcrylicBlur'
    depth_of_fields.Parent = Lighting
    
    for _, object in ipairs(Lighting:GetChildren()) do
        if object:IsA('DepthOfFieldEffect') and object ~= depth_of_fields then
            Connections[object] = object:GetPropertyChangedSignal('FarIntensity'):Connect(function()
                object.FarIntensity = 0
            end)
            object.FarIntensity = 0
        end
    end
end

function AcrylicBlur:create_frame()
    local frame = Instance.new('Frame')
    frame.Size = UDim2.new(1, 0, 1, 0)
    frame.Position = UDim2.new(0.5, 0, 0.5, 0)
    frame.AnchorPoint = Vector2.new(0.5, 0.5)
    frame.BackgroundTransparency = 1
    frame.Parent = self._object
    self._frame = frame
end

function AcrylicBlur:create_root()
    local part = Instance.new('Part')
    part.Name = 'Root'
    part.Color = Color3.new(0, 0, 0)
    part.Material = Enum.Material.Glass
    part.Size = Vector3.new(1, 1, 0)
    part.Anchored = true
    part.CanCollide = false
    part.CanQuery = false
    part.Locked = true
    part.CastShadow = false
    part.Transparency = 0.98
    part.Parent = self._folder
    local specialMesh = Instance.new('SpecialMesh')
    specialMesh.MeshType = Enum.MeshType.Brick
    specialMesh.Offset = Vector3.new(0, 0, -0.000001)
    specialMesh.Parent = part
    self._root = part
end

function AcrylicBlur:setup()
    self:create_depth_of_fields()
    self:create_folder()
    self:create_root()
    self:create_frame()
    self:render(0.001)
    self:check_quality_level()
end

function AcrylicBlur:render(distance)
    local positions = {
        top_left = Vector2.new(),
        top_right = Vector2.new(),
        bottom_right = Vector2.new(),
    }
    local function update_positions(size, position)
        positions.top_left = position
        positions.top_right = position + Vector2.new(size.X, 0)
        positions.bottom_right = position + size
    end
    local function update()
        local top_left = positions.top_left
        local top_right = positions.top_right
        local bottom_right = positions.bottom_right
        local top_left3D = Util:viewport_point_to_world(top_left, distance)
        local top_right3D = Util:viewport_point_to_world(top_right, distance)
        local bottom_right3D = Util:viewport_point_to_world(bottom_right, distance)
        local width = (top_right3D - top_left3D).Magnitude
        local height = (top_right3D - bottom_right3D).Magnitude
        if not self._root then
            return
        end
        self._root.CFrame = CFrame.fromMatrix((top_left3D + bottom_right3D) / 2, Workspace.CurrentCamera.CFrame.XVector, Workspace.CurrentCamera.CFrame.YVector, Workspace.CurrentCamera.CFrame.ZVector)
        self._root.Mesh.Scale = Vector3.new(width, height, 0)
    end
    local function on_change()
        local offset = Util:get_offset()
        local size = self._frame.AbsoluteSize - Vector2.new(offset, offset)
        local position = self._frame.AbsolutePosition + Vector2.new(offset / 2, offset / 2)
        update_positions(size, position)
        task.spawn(update)
    end
    Connections['cframe_update'] = Workspace.CurrentCamera:GetPropertyChangedSignal('CFrame'):Connect(update)
    Connections['viewport_size_update'] = Workspace.CurrentCamera:GetPropertyChangedSignal('ViewportSize'):Connect(update)
    Connections['field_of_view_update'] = Workspace.CurrentCamera:GetPropertyChangedSignal('FieldOfView'):Connect(update)
    Connections['frame_absolute_position'] = self._frame:GetPropertyChangedSignal('AbsolutePosition'):Connect(on_change)
    Connections['frame_absolute_size'] = self._frame:GetPropertyChangedSignal('AbsoluteSize'):Connect(on_change)
    task.spawn(update)
end

function AcrylicBlur:check_quality_level()
    local game_settings = UserSettings().GameSettings
    local quality_level = game_settings.SavedQualityLevel.Value
    if quality_level < 8 then
        self:change_visiblity(false)
    end
    Connections['quality_level'] = game_settings:GetPropertyChangedSignal('SavedQualityLevel'):Connect(function()
        local game_settings = UserSettings().GameSettings
        local quality_level = game_settings.SavedQualityLevel.Value
        self:change_visiblity(quality_level >= 8)
    end)
end

function AcrylicBlur:change_visiblity(state)
    self._root.Transparency = state and 0.98 or 1
end

local Config = setmetatable({
    save = function(self, file_name, config)
        local success_save, result = pcall(function()
            local flags = HttpService:JSONEncode(config)
            writefile(LYRA_CONFIG_FOLDER .. '/' .. file_name .. '.json', flags)
        end)
        if not success_save then
            warn('failed to save config', result)
        end
    end,
    load = function(self, file_name, config)
        local success_load, result = pcall(function()
            if not isfile(LYRA_CONFIG_FOLDER .. '/' .. file_name .. '.json') then
                self:save(file_name, config)
                return
            end
            local flags = readfile(LYRA_CONFIG_FOLDER .. '/' .. file_name .. '.json')
            if not flags then
                self:save(file_name, config)
                return
            end
            return HttpService:JSONDecode(flags)
        end)
        if not success_load then
            warn('failed to load config', result)
        end
        if not result then
            result = {
                _flags = {},
                _keybinds = {},
                _library = {}
            }
        end
        return result
    end
}, Config)

local System = {
    __properties = {
        __autoparry_enabled = false,
        __triggerbot_enabled = false,
        __manual_spam_enabled = false,
        __auto_spam_enabled = false,
        __play_animation = false,
        __accuracy = 1,
        __divisor_multiplier = 1.1,
        __parried = false,
        __training_parried = false,
        __spam_threshold = 1.5,
        __parries = 0,
        __grab_animation = nil,
        __tornado_time = tick(),
        __connections = {},
        __spam_accumulator = 0,
        __spam_rate = 240,
        __infinity_active = false,
        __deathslash_active = false,
        __timehole_active = false,
        __slashesoffury_active = false,
        __slashesoffury_count = 0,
        __is_mobile = UserInputService.TouchEnabled and not UserInputService.MouseEnabled,
        __mobile_guis = {}
    },
    
    __config = {
        __detections = {
            __infinity = false,
            __deathslash = false,
            __timehole = false,
            __slashesoffury = false,
            __phantom = false
        }
    },
    
    __triggerbot = {
        __enabled = false,
        __is_parrying = false,
        __parries = 0,
        __max_parries = 10000,
        __parry_delay = 0.5
    }
}

local PF = nil
local SC = nil

if ReplicatedStorage:FindFirstChild("Controllers") then
    for _, child in ipairs(ReplicatedStorage.Controllers:GetChildren()) do
        if child.Name:match("^SwordsController%s*$") then
            SC = child
        end
    end
end

if LocalPlayer.PlayerGui:FindFirstChild("Hotbar") and LocalPlayer.PlayerGui.Hotbar:FindFirstChild("Block") then
    for _, v in next, getconnections(LocalPlayer.PlayerGui.Hotbar.Block.Activated) do
        if SC and getfenv(v.Function).script == SC then
            PF = v.Function
            break
        end
    end
end

local function update_divisor()
    System.__properties.__divisor_multiplier = 0.75 + (System.__properties.__accuracy - 1) * (3 / 99)
end

System.animation = {}

function System.animation.play_grab_parry()
    if not System.__properties.__play_animation then return end
    
    local character = LocalPlayer.Character
    if not character then return end
    
    local humanoid = character:FindFirstChildOfClass('Humanoid')
    local animator = humanoid and humanoid:FindFirstChildOfClass('Animator')
    if not humanoid or not animator then return end
    
    local sword_name
    if getgenv().skinChangerEnabled then
        sword_name = getgenv().swordAnimations
    else
        sword_name = character:GetAttribute('CurrentlyEquippedSword')
    end
    if not sword_name then return end
    
    local sword_api = ReplicatedStorage.Shared.SwordAPI.Collection
    local parry_animation = sword_api.Default:FindFirstChild('GrabParry')
    if not parry_animation then return end
    
    local sword_data = ReplicatedStorage.Shared.ReplicatedInstances.Swords.GetSword:Invoke(sword_name)
    if not sword_data or not sword_data['AnimationType'] then return end
    
    for _, object in pairs(sword_api:GetChildren()) do
        if object.Name == sword_data['AnimationType'] then
            if object:FindFirstChild('GrabParry') or object:FindFirstChild('Grab') then
                local animation_type = object:FindFirstChild('GrabParry') and 'GrabParry' or 'Grab'
                parry_animation = object[animation_type]
            end
        end
    end
    
    if System.__properties.__grab_animation and System.__properties.__grab_animation.IsPlaying then
        System.__properties.__grab_animation:Stop()
    end
    
    System.__properties.__grab_animation = animator:LoadAnimation(parry_animation)
    System.__properties.__grab_animation.Priority = Enum.AnimationPriority.Action4
    System.__properties.__grab_animation:Play()
end

System.ball = {}

function System.ball.get()
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return nil end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:GetAttribute('realBall') then
            ball.CanCollide = false
            return ball
        end
    end
    return nil
end

function System.ball.get_all()
    local balls_table = {}
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return balls_table end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:GetAttribute('realBall') then
            ball.CanCollide = false
            table.insert(balls_table, ball)
        end
    end
    return balls_table
end

System.player = {}
local Closest_Entity = nil

function System.player.get_closest()
    local max_distance = math.huge
    local closest_entity = nil
    if not Alive then return nil end
    for _, entity in pairs(Alive:GetChildren()) do
        if entity ~= LocalPlayer.Character and entity.PrimaryPart then
            local distance = LocalPlayer:DistanceFromCharacter(entity.PrimaryPart.Position)
            if distance < max_distance then
                max_distance = distance
                closest_entity = entity
            end
        end
    end
    Closest_Entity = closest_entity
    return closest_entity
end

System.parry = {}
function System.parry.execute()
    if System.__properties.__parries > 10000 or not LocalPlayer.Character then return end
    
    local method = getgenv().AutoParryMode or "Combined (Mobile)"
    
    if method == "Combined (Mobile)" then
        -- Execute all methods for maximum reliability on mobile
        pcall(function()
            -- 1. Direct function call if found
            if PF then
                task.spawn(function() pcall(PF) end)
            end
            
            -- 2. Fire block button UI connections
            local blockButton = LocalPlayer.PlayerGui:FindFirstChild("Hotbar") and LocalPlayer.PlayerGui.Hotbar:FindFirstChild("Block")
            if blockButton then
                for _, eventName in ipairs({"Activated", "MouseButton1Click", "MouseButton1Down", "TouchTap"}) do
                    local event = blockButton:FindFirstChild(eventName) or blockButton[eventName]
                    if event then
                        for _, connection in ipairs(getconnections(event)) do
                            pcall(function() connection:Fire() end)
                        end
                    end
                end
            end
            
            -- 3. Screen tap emulation
            local camera = workspace.CurrentCamera
            local viewportSize = camera and camera.ViewportSize or Vector2.new(800, 600)
            local clickX, clickY = viewportSize.X / 2, viewportSize.Y / 2
            
            if VIM and VIM.SendTouchEvent then
                VIM:SendTouchEvent(1, 0, clickX, clickY) -- State 0: TouchBegan
                task.wait()
                VIM:SendTouchEvent(1, 2, clickX, clickY) -- State 2: TouchEnded
            elseif VIM and VIM.SendMouseButtonEvent then
                VIM:SendMouseButtonEvent(clickX, clickY, 0, true, game, 1)
                task.wait()
                VIM:SendMouseButtonEvent(clickX, clickY, 0, false, game, 1)
            end
        end)
    elseif method == "Screen Tap" then
        pcall(function()
            local camera = workspace.CurrentCamera
            local viewportSize = camera and camera.ViewportSize or Vector2.new(800, 600)
            local clickX, clickY = viewportSize.X / 2, viewportSize.Y / 2
            
            if VIM and VIM.SendTouchEvent then
                VIM:SendTouchEvent(1, 0, clickX, clickY)
                task.wait()
                VIM:SendTouchEvent(1, 2, clickX, clickY)
            elseif VIM and VIM.SendMouseButtonEvent then
                VIM:SendMouseButtonEvent(clickX, clickY, 0, true, game, 1)
                task.wait()
                VIM:SendMouseButtonEvent(clickX, clickY, 0, false, game, 1)
            end
        end)
    elseif method == "Block Button" then
        pcall(function()
            local blockButton = LocalPlayer.PlayerGui:FindFirstChild("Hotbar") and LocalPlayer.PlayerGui.Hotbar:FindFirstChild("Block")
            if blockButton then
                for _, eventName in ipairs({"Activated", "MouseButton1Click", "MouseButton1Down", "TouchTap"}) do
                    local event = blockButton:FindFirstChild(eventName) or blockButton[eventName]
                    if event then
                        for _, connection in ipairs(getconnections(event)) do
                            pcall(function() connection:Fire() end)
                        end
                    end
                end
            end
        end)
    elseif method == "Direct Call (PF)" then
        if PF then
            pcall(PF)
        end
    elseif method == "KeyPress (F)" then
        pcall(function()
            if keypress and keyrelease then
                keypress(0x46)
                task.wait()
                keyrelease(0x46)
            else
                VIM:SendKeyEvent(true, Enum.KeyCode.F, false, game)
                task.wait()
                VIM:SendKeyEvent(false, Enum.KeyCode.F, false, game)
            end
        end)
    elseif method == "MouseClick" then
        if mouse1click then
            pcall(mouse1click)
        else
            pcall(function()
                VIM:SendMouseButtonEvent(0, 0, 0, true, game, 0)
                task.wait()
                VIM:SendMouseButtonEvent(0, 0, 0, false, game, 0)
            end)
        end
    end
    
    if System.__properties.__parries <= 10000 then
        System.__properties.__parries = System.__properties.__parries + 1
        task.delay(0.5, function()
            if System.__properties.__parries > 0 then System.__properties.__parries = System.__properties.__parries - 1 end
        end)
    end
end

function System.parry.keypress()
    System.parry.execute()
end

function System.parry.execute_action()
    System.animation.play_grab_parry()
    System.parry.execute()
end

local function linear_predict(a, b, time_volume) return a + (b - a) * time_volume end

System.detection = {
    __ball_properties = { __aerodynamic_time = tick(), __last_warping = tick(), __lerp_radians = 0, __curving = tick() }
}

function System.detection.is_curved()
    local ball_properties = System.detection.__ball_properties
    local ball = System.ball.get()
    if not ball then return false end
    
    local zoomies = ball:FindFirstChild('zoomies')
    if not zoomies then return false end
    
    local velocity = zoomies.VectorVelocity
    local ball_direction = velocity.Unit
    local direction = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Unit
    local dot = direction:Dot(ball_direction)
    
    local speed = velocity.Magnitude
    local speed_threshold = math.min(speed / 100, 40)
    
    local direction_difference = (ball_direction - velocity).Unit
    local direction_similarity = direction:Dot(direction_difference)
    local dot_difference = dot - direction_similarity
    local distance = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Magnitude
    
    local ping = Stats.Network.ServerStatsItem['Data Ping']:GetValue()
    local dot_threshold = 0.5 - (ping / 1000)
    local reach_time = distance / speed - (ping / 1000)
    local ball_distance_threshold = 15 - math.min(distance / 1000, 15) + speed_threshold
    
    local clamped_dot = math.clamp(dot, -1, 1)
    local radians = math.rad(math.asin(clamped_dot))
    
    ball_properties.__lerp_radians = linear_predict(ball_properties.__lerp_radians, radians, 0.8)
    
    if speed > 0 and reach_time > ping / 10 then ball_distance_threshold = math.max(ball_distance_threshold - 15, 15) end
    if distance < ball_distance_threshold then return false end
    if dot_difference < dot_threshold then return true end
    
    if ball_properties.__lerp_radians < 0.018 then ball_properties.__last_warping = tick() end
    if (tick() - ball_properties.__last_warping) < (reach_time / 1.5) then return true end
    if (tick() - ball_properties.__curving) < (reach_time / 1.5) then return true end
    
    return dot < dot_threshold
end

ReplicatedStorage.Remotes.DeathBall.OnClientEvent:Connect(function(_, d) System.__properties.__deathslash_active = d or false end)
ReplicatedStorage.Remotes.InfinityBall.OnClientEvent:Connect(function(_, b) System.__properties.__infinity_active = b or false end)

pcall(function()
    local net = ReplicatedStorage.Packages._Index["sleitnick_net@0.1.0"].net
    net["RE/TimeHoleActivate"].OnClientEvent:Connect(function(player)
        if player == LocalPlayer or player == LocalPlayer.Name or (player and player.Name == LocalPlayer.Name) then
            System.__properties.__timehole_active = true
        end
    end)
    net["RE/TimeHoleDeactivate"].OnClientEvent:Connect(function() System.__properties.__timehole_active = false end)
    
    net["RE/SlashesOfFuryActivate"].OnClientEvent:Connect(function(player)
        if player == LocalPlayer or player == LocalPlayer.Name or (player and player.Name == LocalPlayer.Name) then
            System.__properties.__slashesoffury_active = true
            System.__properties.__slashesoffury_count = 0
        end
    end)
    net["RE/SlashesOfFuryEnd"].OnClientEvent:Connect(function()
        System.__properties.__slashesoffury_active = false
        System.__properties.__slashesoffury_count = 0
    end)
    net["RE/SlashesOfFuryParry"].OnClientEvent:Connect(function() System.__properties.__slashesoffury_count = System.__properties.__slashesoffury_count + 1 end)
    net["RE/SlashesOfFuryCatch"].OnClientEvent:Connect(function()
        task.spawn(function()
            while System.__properties.__slashesoffury_active and System.__properties.__slashesoffury_count < 36 do
                if System.__config.__detections.__slashesoffury then
                    System.parry.execute()
                    task.wait(0.05)
                else break end
            end
        end)
    end)
end)

Runtime.ChildAdded:Connect(function(Object)
    if System.__config.__detections.__phantom and (Object.Name == "maxTransmission" or Object.Name == "transmissionpart") then
        local Weld = Object:FindFirstChildWhichIsA("WeldConstraint")
        local Character = LocalPlayer.Character
        if Weld and Character and Weld.Part1 == Character:FindFirstChild("HumanoidRootPart") then
            local CurrentBall = System.ball.get()
            Weld:Destroy()
            if CurrentBall then
                local FocusConnection
                FocusConnection = RunService.RenderStepped:Connect(function()
                    local Highlighted = CurrentBall:GetAttribute("highlighted")
                    if Highlighted == true then
                        ReplicatedStorage.Remotes.AbilityButtonPress:Fire()
                        System.__properties.__parried = true
                        task.delay(1, function() System.__properties.__parried = false end)
                    elseif Highlighted == false then
                        FocusConnection:Disconnect()
                    end
                end)
                task.delay(3, function() if FocusConnection and FocusConnection.Connected then FocusConnection:Disconnect() end end)
            end
        end
    end
end)

System.triggerbot = {}
function System.triggerbot.trigger(ball)
    if System.__triggerbot.__is_parrying or System.__triggerbot.__parries > System.__triggerbot.__max_parries then return end
    if LocalPlayer.Character and LocalPlayer.Character.PrimaryPart and LocalPlayer.Character.PrimaryPart:FindFirstChild('SingularityCape') then return end
    
    System.__triggerbot.__is_parrying = true
    System.__triggerbot.__parries = System.__triggerbot.__parries + 1
    
    System.animation.play_grab_parry()
    System.parry.execute()
    
    task.delay(System.__triggerbot.__parry_delay, function()
        if System.__triggerbot.__parries > 0 then System.__triggerbot.__parries = System.__triggerbot.__parries - 1 end
    end)
    
    local connection
    connection = ball:GetAttributeChangedSignal('target'):Once(function()
        System.__triggerbot.__is_parrying = false
        if connection then connection:Disconnect() end
    end)
    
    task.spawn(function()
        local start_time = tick()
        repeat RunService.Heartbeat:Wait() until (tick() - start_time >= 1 or not System.__triggerbot.__is_parrying)
        System.__triggerbot.__is_parrying = false
    end)
end

function System.triggerbot.loop()
    if not System.__triggerbot.__enabled then return end
    if LocalPlayer.Character and LocalPlayer.Character.PrimaryPart and LocalPlayer.Character.PrimaryPart:FindFirstChild('SingularityCape') then return end
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:IsA('BasePart') and ball:GetAttribute('target') == LocalPlayer.Name then
            System.triggerbot.trigger(ball)
            break
        end
    end
end

function System.triggerbot.enable(enabled)
    System.__triggerbot.__enabled = enabled
    if enabled then
        if not System.__properties.__connections.__triggerbot then System.__properties.__connections.__triggerbot = RunService.Heartbeat:Connect(System.triggerbot.loop) end
    else
        if System.__properties.__connections.__triggerbot then
            System.__properties.__connections.__triggerbot:Disconnect()
            System.__properties.__connections.__triggerbot = nil
        end
        System.__triggerbot.__is_parrying = false
        System.__triggerbot.__parries = 0
    end
end

System.manual_spam = {}
getgenv().is_holding_spam_key = false

UserInputService.InputBegan:Connect(function(input, gp)
    if gp then return end
    if input.UserInputType == Enum.UserInputType.Keyboard then
        local keyName = input.KeyCode.Name
        if keyName == (getgenv().HoldSpamKey or "V") then
            getgenv().is_holding_spam_key = true
        end
    end
end)

UserInputService.InputEnded:Connect(function(input, gp)
    if input.UserInputType == Enum.UserInputType.Keyboard then
        local keyName = input.KeyCode.Name
        if keyName == (getgenv().HoldSpamKey or "V") then
            getgenv().is_holding_spam_key = false
        end
    end
end)

function System.manual_spam.loop(delta)
    local should_spam = System.__properties.__manual_spam_enabled or (getgenv().HoldSpamEnabled and getgenv().is_holding_spam_key)
    
    if not should_spam or not LocalPlayer.Character or LocalPlayer.Character.Parent ~= Alive then return end
    System.__properties.__spam_accumulator = System.__properties.__spam_accumulator + delta
    local interval = 1 / System.__properties.__spam_rate
    if System.__properties.__spam_accumulator >= interval then
        System.__properties.__spam_accumulator = 0
        System.parry.execute()
        if getgenv().ManualSpamAnimationFix and PF then pcall(PF) end
    end
end

RunService.Heartbeat:Connect(System.manual_spam.loop)

System.auto_spam = {}
function System.auto_spam:get_entity_properties()
    System.player.get_closest()
    if not Closest_Entity then return false end
    return { Velocity = Closest_Entity.PrimaryPart.Velocity, Direction = (LocalPlayer.Character.PrimaryPart.Position - Closest_Entity.PrimaryPart.Position).Unit, Distance = (LocalPlayer.Character.PrimaryPart.Position - Closest_Entity.PrimaryPart.Position).Magnitude }
end

function System.auto_spam:get_ball_properties()
    local ball = System.ball.get()
    if not ball then return false end
    local ball_velocity = Vector3.zero
    local ball_direction = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Unit
    return { Velocity = ball_velocity, Direction = ball_direction, Distance = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Magnitude, Dot = ball_direction:Dot(ball_velocity.Unit) }
end

function System.auto_spam.spam_service(self)
    local ball = System.ball.get()
    local entity = System.player.get_closest()
    if not ball or not entity or not entity.PrimaryPart then return 0 end
    
    local speed = ball.AssemblyLinearVelocity.Magnitude
    local dot = ((LocalPlayer.Character.PrimaryPart.Position - ball.Position).Unit):Dot(ball.AssemblyLinearVelocity.Unit)
    local target_distance = LocalPlayer:DistanceFromCharacter(entity.PrimaryPart.Position)
    local maximum_spam_distance = self.Ping + math.min(speed / 6, 255)
    
    if self.Entity_Properties.Distance > maximum_spam_distance or self.Ball_Properties.Distance > maximum_spam_distance or target_distance > maximum_spam_distance then return 0 end
    local maximum_dot = math.clamp(dot, -1, 0) * (5 - math.min(speed / 5, 5))
    return maximum_spam_distance - maximum_dot
end

function System.auto_spam.start()
    if System.__properties.__connections.__auto_spam then System.__properties.__connections.__auto_spam:Disconnect() end
    System.__properties.__auto_spam_enabled = true
    System.__properties.__connections.__auto_spam = RunService.PreSimulation:Connect(function()
        local ball = System.ball.get()
        if not ball or System.__properties.__slashesoffury_active then return end
        local zoomies = ball:FindFirstChild('zoomies')
        if not zoomies then return end
        
        System.player.get_closest()
        local ping = Stats.Network.ServerStatsItem['Data Ping']:GetValue()
        local ping_threshold = math.clamp(ping / 10, 1, 16)
        local ball_target = ball:GetAttribute('target')
        local ball_props = System.auto_spam:get_ball_properties()
        local entity_props = System.auto_spam:get_entity_properties()
        
        if not ball_props or not entity_props or not ball_target then return end
        local spam_accuracy = System.auto_spam.spam_service({Ball_Properties = ball_props, Entity_Properties = entity_props, Ping = ping_threshold})
        local target_distance = LocalPlayer:DistanceFromCharacter(Closest_Entity.PrimaryPart.Position)
        local distance = LocalPlayer:DistanceFromCharacter(ball.Position)
        
        if target_distance > spam_accuracy or distance > spam_accuracy then return end
        if LocalPlayer.Character:GetAttribute('Pulsed') then return end
        if ball_target == LocalPlayer.Name and target_distance > 30 and distance > 30 then return end
        
        if distance <= spam_accuracy and System.__properties.__parries > System.__properties.__spam_threshold then
            System.parry.execute()
            if getgenv().AutoSpamAnimationFix and PF then pcall(PF) end
        end
    end)
end

function System.auto_spam.stop()
    System.__properties.__auto_spam_enabled = false
    if System.__properties.__connections.__auto_spam then System.__properties.__connections.__auto_spam:Disconnect(); System.__properties.__connections.__auto_spam = nil end
end

System.autoparry = {}
function System.autoparry.start()
    if System.__properties.__connections.__autoparry then System.__properties.__connections.__autoparry:Disconnect() end
    System.__properties.__connections.__autoparry = RunService.PreSimulation:Connect(function()
        if not System.__properties.__autoparry_enabled or not LocalPlayer.Character or not LocalPlayer.Character.PrimaryPart then return end
        
        local balls = System.ball.get_all()
        local one_ball = System.ball.get()
        local training_ball = Workspace:FindFirstChild("TrainingBalls") and Workspace.TrainingBalls:FindFirstChildWhichIsA("BasePart")
        
        for _, ball in pairs(balls) do
            local skip = false
            
            if System.__triggerbot.__enabled or getgenv().BallVelocityAbove800 or not ball then 
                skip = true 
            end
            
            local zoomies
            if not skip then
                zoomies = ball:FindFirstChild('zoomies')
                if not zoomies then skip = true end
            end
            
            if not skip then
                ball:GetAttributeChangedSignal('target'):Once(function() System.__properties.__parried = false end)
                if System.__properties.__parried then skip = true end
            end
            
            if not skip then
                local ball_target = ball:GetAttribute('target')
                local velocity = zoomies.VectorVelocity
                local distance = (LocalPlayer.Character.PrimaryPart.Position - ball.Position).Magnitude
                local ping_threshold = math.clamp(Stats.Network.ServerStatsItem['Data Ping']:GetValue() / 100, 5, 17)
                local speed = velocity.Magnitude
                
                local speed_divisor = (2.4 + math.min(math.max(speed - 9.5, 0), 650) * 0.002) * System.__properties.__divisor_multiplier
                local parry_accuracy = ping_threshold + math.max(speed / speed_divisor, 9.5)
                local curved = System.detection.is_curved()
                
                if ball:FindFirstChild('AeroDynamicSlashVFX') then 
                    ball.AeroDynamicSlashVFX:Destroy()
                    System.__properties.__tornado_time = tick() 
                end
                
                if Runtime:FindFirstChild('Tornado') and (tick() - System.__properties.__tornado_time) < (Runtime.Tornado:GetAttribute('TornadoTime') or 1) + 0.314159 then skip = true end
                if not skip and one_ball and one_ball:GetAttribute('target') == LocalPlayer.Name and curved then skip = true end
                if not skip and (ball:FindFirstChild('ComboCounter') or LocalPlayer.Character.PrimaryPart:FindFirstChild('SingularityCape')) then skip = true end
                if not skip and ((System.__config.__detections.__infinity and System.__properties.__infinity_active) or (System.__config.__detections.__deathslash and System.__properties.__deathslash_active) or (System.__config.__detections.__timehole and System.__properties.__timehole_active) or (System.__config.__detections.__slashesoffury and System.__properties.__slashesoffury_active)) then skip = true end
                
                if not skip and ball_target == LocalPlayer.Name and distance <= parry_accuracy then
                    if getgenv().CooldownProtection and LocalPlayer.PlayerGui.Hotbar.Block.UIGradient.Offset.Y < 0.4 then 
                        ReplicatedStorage.Remotes.AbilityButtonPress:Fire()
                        skip = true 
                    end
                    
                    if not skip and getgenv().AutoAbility and LocalPlayer.PlayerGui.Hotbar.Ability.UIGradient.Offset.Y == 0.5 then
                        local abs = LocalPlayer.Character:FindFirstChild("Abilities")
                        if abs then
                            for _, ab in pairs({"Raging Deflection", "Rapture", "Calming Deflection", "Aerodynamic Slash", "Fracture", "Death Slash"}) do
                                if abs:FindFirstChild(ab) and abs[ab].Enabled then
                                    System.__properties.__parried = true
                                    ReplicatedStorage.Remotes.AbilityButtonPress:Fire()
                                    task.wait(2.432)
                                    ReplicatedStorage:WaitForChild("Remotes"):WaitForChild("DeathSlashShootActivation"):FireServer(true)
                                    break
                                end
                            end
                            if System.__properties.__parried then skip = true end
                        end
                    end
                    
                    if not skip then
                        System.parry.execute_action()
                        System.__properties.__parried = true
                    end
                end
                
                if not skip then
                    local last_parrys = tick()
                    repeat RunService.Stepped:Wait() until (tick() - last_parrys) >= 1 or not System.__properties.__parried
                    System.__properties.__parried = false
                end
            end
        end

        if training_ball and training_ball:GetAttribute("realBall") then
            local zoomies = training_ball:FindFirstChild('zoomies')
            if zoomies then
                training_ball:GetAttributeChangedSignal('target'):Once(function() System.__properties.__training_parried = false end)
                if not System.__properties.__training_parried then
                    local speed = zoomies.VectorVelocity.Magnitude
                    local distance = LocalPlayer:DistanceFromCharacter(training_ball.Position)
                    local ping_threshold = math.clamp(Stats.Network.ServerStatsItem['Data Ping']:GetValue() / 100, 5, 17)
                    local speed_divisor = (2.4 + math.min(math.max(speed - 9.5, 0), 650) * 0.002) * System.__properties.__divisor_multiplier
                    
                    if training_ball:GetAttribute('target') == LocalPlayer.Name and distance <= (ping_threshold + math.max(speed / speed_divisor, 9.5)) then
                        System.parry.execute_action()
                        System.__properties.__training_parried = true
                        local last_parrys = tick()
                        repeat RunService.Stepped:Wait() until (tick() - last_parrys) >= 1 or not System.__properties.__training_parried
                        System.__properties.__training_parried = false
                    end
                end
            end
        end
    end)
end

function System.autoparry.stop()
    if System.__properties.__connections.__autoparry then System.__properties.__connections.__autoparry:Disconnect(); System.__properties.__connections.__autoparry = nil end
end

local animation_system = {
    storage = {},
    current = nil,
    track = nil
}

function animation_system.load_animations()
    pcall(function()
        local function find_emotes(parent)
            for _, child in ipairs(parent:GetChildren()) do
                if child:IsA("Folder") and child.Name:lower() == "emotes" then
                    return child
                else
                    local found = find_emotes(child)
                    if found then return found end
                end
            end
            return nil
        end
        
        local emotes_folder = find_emotes(ReplicatedStorage)
        
        if emotes_folder then
            for _, animation in ipairs(emotes_folder:GetChildren()) do
                if animation:IsA("Animation") then
                    local emote_name = animation:GetAttribute("EmoteName") or animation.Name
                    animation_system.storage[emote_name] = animation
                end
            end
        else
            for _, obj in ipairs(ReplicatedStorage:GetDescendants()) do
                if obj:IsA("Animation") and (obj.Name:lower():match("emote") or obj.Name:lower():match("dance") or obj.Name:lower():match("floss")) then
                    local emote_name = obj:GetAttribute("EmoteName") or obj.Name
                    animation_system.storage[emote_name] = obj
                end
            end
        end
    end)
end

function animation_system.get_emotes_list()
    -- Hardcoded fallback list ensuring dropdown ALWAYS works
    local emotes_list = {"None", "Sit", "DefaultDance", "Floss", "Griddy", "TakeTheL", "Goku", "Lobby", "Dance", "Clap", "SideStep", "Shuffle", "Tantrum", "Robot", "SillyDance", "Spin", "Hypnotize", "Moonwalk"}
    
    for emote_name in pairs(animation_system.storage) do
        local found = false
        for _, existing in ipairs(emotes_list) do
            if string.lower(existing) == string.lower(emote_name) then found = true; break; end
        end
        if not found then table.insert(emotes_list, emote_name) end
    end
    
    table.sort(emotes_list)
    
    -- Guarantee 'None' stays strictly at the top of the array
    local none_idx = nil
    for i, v in ipairs(emotes_list) do
        if v == "None" then
            none_idx = i
            break
        end
    end
    if none_idx then table.remove(emotes_list, none_idx) end
    table.insert(emotes_list, 1, "None")
    
    return emotes_list
end

function animation_system.play(emote_name)
    local animation_data = animation_system.storage[emote_name]
    
    -- Dynamic aggressive search if emote isn't in storage yet but is requested via fallback/search
    if not animation_data then
        for _, obj in ipairs(ReplicatedStorage:GetDescendants()) do
            if obj:IsA("Animation") then
                local en = obj:GetAttribute("EmoteName") or obj.Name
                if string.lower(en) == string.lower(emote_name) then
                    animation_data = obj
                    animation_system.storage[emote_name] = obj
                    break
                end
            end
        end
    end
    
    if not animation_data or not LocalPlayer.Character then return false end
    
    local humanoid = LocalPlayer.Character:FindFirstChild("Humanoid")
    if not humanoid then return false end
    
    local animator = humanoid:FindFirstChild("Animator")
    if not animator then return false end
    
    if animation_system.track then
        animation_system.track:Stop()
        animation_system.track:Destroy()
    end
    
    animation_system.track = animator:LoadAnimation(animation_data)
    animation_system.track:Play()
    animation_system.current = emote_name
    return true
end

function animation_system.stop()
    if animation_system.track then
        animation_system.track:Stop()
        animation_system.track:Destroy()
        animation_system.track = nil
    end
    animation_system.current = nil
end

function animation_system.start()
    if not System.__properties.__connections.animations then
        System.__properties.__connections.animations = RunService.Heartbeat:Connect(function()
            if not LocalPlayer.Character or not LocalPlayer.Character.PrimaryPart then return end
            
            local speed = LocalPlayer.Character.PrimaryPart.AssemblyLinearVelocity.Magnitude
            if speed > 30 and getgenv().AutoStopEmote then
                if animation_system.track and animation_system.track.IsPlaying then
                    animation_system.track:Stop()
                end
            else
                if animation_system.current and (not animation_system.track or not animation_system.track.IsPlaying) then
                    animation_system.play(animation_system.current)
                end
            end
        end)
    end
end

function animation_system.cleanup()
    animation_system.stop()
    if System.__properties.__connections.animations then
        System.__properties.__connections.animations:Disconnect()
        System.__properties.__connections.animations = nil
    end
end

animation_system.load_animations()
local emotes_data = animation_system.get_emotes_list()

local WalkableSemiImmortal = {}
local immortalState = { enabled = false, notify = false, heartbeatConnection = nil }
local immortalDesyncData = { originalCFrame = nil, originalVelocity = nil }
local immortalCache = { character = nil, hrp = nil, head = nil, headOffset = Vector3.new(0, 0, 0), aliveFolder = nil }
local immortalHooks = { oldIndex = nil }
local immortalConstants = { emptyCFrame = CFrame.new(), radius = 25, baseHeight = 5, riseHeight = 30, cycleSpeed = 11.9, velocity = Vector3.new(1, 1, 1) }

local function immortalUpdateCache()
    local character = LocalPlayer.Character
    if not character then
        immortalCache.character = nil
        immortalCache.hrp = nil
        immortalCache.head = nil
        return
    end
    
    if character ~= immortalCache.character or not immortalCache.hrp or not immortalCache.head then
        immortalCache.character = character
        immortalCache.hrp = character:FindFirstChild("HumanoidRootPart")
        immortalCache.head = character:FindFirstChild("Head")
        
        if immortalCache.hrp then
            immortalCache.headOffset = Vector3.new(0, immortalCache.hrp.Size.Y * 0.5 + 0.5, 0)
        end
    end
end

local function immortalIsInAliveFolder()
    local aliveFolder = Workspace:FindFirstChild("Alive")
    return aliveFolder and immortalCache.character and immortalCache.character.Parent == aliveFolder
end

local function immortalCalculateOrbitPosition(hrp)
    local angle = math.random(-2147483647, 2147483647) * 1000
    local cycle = math.floor(tick() * immortalConstants.cycleSpeed) % 2
    local yOffset = cycle == 0 and 0 or immortalConstants.riseHeight
    
    local pos = hrp.Position
    local yBase = pos.Y - hrp.Size.Y * 0.5 + immortalConstants.baseHeight + yOffset
    
    return CFrame.new(pos.X + math.cos(angle) * immortalConstants.radius, yBase, pos.Z + math.sin(angle) * immortalConstants.radius)
end

local function performImmortalDesync()
    immortalUpdateCache()
    if not immortalState.enabled or not immortalCache.hrp or not immortalIsInAliveFolder() then return end
    
    local hrp = immortalCache.hrp
    immortalDesyncData.originalCFrame = hrp.CFrame
    immortalDesyncData.originalVelocity = hrp.AssemblyLinearVelocity
    
    hrp.CFrame = immortalCalculateOrbitPosition(hrp)
    hrp.AssemblyLinearVelocity = immortalConstants.velocity
    
    RunService.RenderStepped:Wait()
    
    hrp.CFrame = immortalDesyncData.originalCFrame
    hrp.AssemblyLinearVelocity = immortalDesyncData.originalVelocity
end

function WalkableSemiImmortal.toggle(enabled)
    if immortalState.enabled == enabled then return end
    immortalState.enabled = enabled
    getgenv().Walkablesemiimortal = enabled
    
    if enabled then
        if not immortalState.heartbeatConnection then
            immortalState.heartbeatConnection = RunService.Heartbeat:Connect(performImmortalDesync)
        end
    else
        if immortalState.heartbeatConnection then
            immortalState.heartbeatConnection:Disconnect()
            immortalState.heartbeatConnection = nil
        end
        immortalDesyncData.originalCFrame = nil
        immortalDesyncData.originalVelocity = nil
    end
end

function WalkableSemiImmortal.setRadius(value) immortalConstants.radius = value end
function WalkableSemiImmortal.setHeight(value) immortalConstants.riseHeight = value end

LocalPlayer.CharacterRemoving:Connect(function()
    immortalCache.character = nil
    immortalCache.hrp = nil
    immortalCache.head = nil
    immortalCache.aliveFolder = nil
    immortalDesyncData.originalCFrame = nil
    immortalDesyncData.originalVelocity = nil
end)

immortalHooks.oldIndex = hookmetamethod(game, "__index", newcclosure(function(self, key)
    if not checkcaller() then
        if immortalState.enabled and key == "CFrame" and immortalCache.hrp and immortalIsInAliveFolder() then
            if self == immortalCache.hrp then
                return immortalDesyncData.originalCFrame or immortalConstants.emptyCFrame
            elseif self == immortalCache.head and immortalDesyncData.originalCFrame then
                return immortalDesyncData.originalCFrame + immortalCache.headOffset
            end
        end
    end
    
    return immortalHooks.oldIndex(self, key)
end))

task.spawn(function()
    local swordInstancesInstance = ReplicatedStorage:FindFirstChild("Shared") and ReplicatedStorage.Shared:FindFirstChild("ReplicatedInstances") and ReplicatedStorage.Shared.ReplicatedInstances:FindFirstChild("Swords")
    if not swordInstancesInstance then return end
    
    local swordInstances = require(swordInstancesInstance)

    local swordsController

    while task.wait(1) and (not swordsController) do
        for i,v in getconnections(ReplicatedStorage.Remotes.FireSwordInfo.OnClientEvent) do
            if v.Function and islclosure(v.Function) then
                local upvalues = getupvalues(v.Function)
                if #upvalues == 1 and type(upvalues[1]) == "table" then
                    swordsController = upvalues[1]
                    break
                end
            end
        end
    end

    function getSlashName(swordName)
        local slashName = nil
        pcall(function() slashName = swordInstances:GetSword(swordName) end)
        return (slashName and slashName.SlashName) or "SlashEffect"
    end

    function setSword()
        if not getgenv().skinChangerEnabled then return end
        
        local char = LocalPlayer.Character
        if not char then return end

        pcall(function() 
            if swordInstances and rawget(swordInstances, "EquipSwordTo") then
                setupvalue(rawget(swordInstances,"EquipSwordTo"), 3, false) 
            end
        end)
        
        if getgenv().changeSwordModel then
            pcall(function() 
                char:SetAttribute("CurrentlyEquippedSword", getgenv().swordModel)
                swordInstances:EquipSwordTo(char, getgenv().swordModel) 
            end)
        end
        
        if getgenv().changeSwordAnimation and swordsController then
            pcall(function() swordsController:SetSword(getgenv().swordAnimations) end)
        end
    end

    local playParryFunc
    local parrySuccessAllConnection

    while task.wait() and not parrySuccessAllConnection do
        for i,v in getconnections(ReplicatedStorage.Remotes.ParrySuccessAll.OnClientEvent) do
            if v.Function and getinfo(v.Function).name == "parrySuccessAll" then
                parrySuccessAllConnection = v
                playParryFunc = v.Function
                v:Disable()
            end
        end
    end

    local parrySuccessClientConnection
    while task.wait() and not parrySuccessClientConnection do
        for i,v in getconnections(ReplicatedStorage.Remotes.ParrySuccessClient.Event) do
            if v.Function and getinfo(v.Function).name == "parrySuccessAll" then
                parrySuccessClientConnection = v
                v:Disable()
            end
        end
    end

    getgenv().slashName = getSlashName(getgenv().swordFX or "Default")

    local lastOtherParryTimestamp = 0
    ReplicatedStorage.Remotes.ParrySuccessAll.OnClientEvent:Connect(function(...)
        setthreadidentity(2)
        local args = {...}
        if tostring(args[4]) ~= LocalPlayer.Name then
            lastOtherParryTimestamp = tick()
        elseif getgenv().skinChangerEnabled and getgenv().changeSwordFX then
            args[1] = getgenv().slashName
            args[3] = getgenv().swordFX
        end
        return playParryFunc(unpack(args))
    end)

    getgenv().updateSword = function()
        if getgenv().changeSwordFX then
            getgenv().slashName = getSlashName(getgenv().swordFX or "Default")
        end
        setSword()
    end

    task.spawn(function()
        while task.wait(1) do
            if getgenv().skinChangerEnabled and getgenv().changeSwordModel then
                local char = LocalPlayer.Character or LocalPlayer.CharacterAdded:Wait()
                if LocalPlayer:GetAttribute("CurrentlyEquippedSword") ~= getgenv().swordModel then
                    setSword()
                end
                if char and (not char:FindFirstChild(getgenv().swordModel)) then
                    setSword()
                end
                for _,v in (char and char:GetChildren()) or {} do
                    if v:IsA("Model") and v.Name ~= getgenv().swordModel then
                        v:Destroy()
                    end
                    task.wait()
                end
            end
        end
    end)
end)

-- ============================================================================
-- VISUALS, ESP, PLAYER MODS
-- ============================================================================
System.visuals = {
    __esp_enabled = false, __esp_team_check = false, __esp_color = Color3.fromRGB(135, 80, 255), __esp_highlight_cache = {},
    __night_mode = false, __night_color = Color3.fromRGB(10, 10, 15),
    __original_lighting = { Ambient = Lighting.Ambient, Brightness = Lighting.Brightness, ClockTime = Lighting.ClockTime }
}

function System.visuals.update_esp()
    if not System.visuals.__esp_enabled then
        for p, h in pairs(System.visuals.__esp_highlight_cache) do h:Destroy(); System.visuals.__esp_highlight_cache[p] = nil end
        return
    end
    for _, player in pairs(Players:GetPlayers()) do
        if player ~= LocalPlayer and player.Character and player.Character:FindFirstChild("HumanoidRootPart") then
            if System.visuals.__esp_team_check and player.Team == LocalPlayer.Team then
                if System.visuals.__esp_highlight_cache[player] then System.visuals.__esp_highlight_cache[player]:Destroy(); System.visuals.__esp_highlight_cache[player] = nil end
            else
                if not System.visuals.__esp_highlight_cache[player] or System.visuals.__esp_highlight_cache[player].Parent ~= player.Character then
                    if System.visuals.__esp_highlight_cache[player] then System.visuals.__esp_highlight_cache[player]:Destroy() end
                    local hl = Instance.new("Highlight")
                    hl.Name = "LyraESP_" .. player.Name; hl.FillColor = System.visuals.__esp_color; hl.OutlineColor = Color3.new(1, 1, 1); hl.FillTransparency = 0.5; hl.OutlineTransparency = 0.2; hl.DepthMode = Enum.HighlightDepthMode.AlwaysOnTop; hl.Parent = player.Character
                    System.visuals.__esp_highlight_cache[player] = hl
                end
                System.visuals.__esp_highlight_cache[player].FillColor = System.visuals.__esp_color
            end
        else
            if System.visuals.__esp_highlight_cache[player] then System.visuals.__esp_highlight_cache[player]:Destroy(); System.visuals.__esp_highlight_cache[player] = nil end
        end
    end
end

function System.visuals.toggle_night_mode(state)
    System.visuals.__night_mode = state
    if state then Lighting.Ambient = System.visuals.__night_color; Lighting.Brightness = 0.2; Lighting.ClockTime = 0
    else Lighting.Ambient = System.visuals.__original_lighting.Ambient; Lighting.Brightness = System.visuals.__original_lighting.Brightness; Lighting.ClockTime = System.visuals.__original_lighting.ClockTime end
end

System.player_mods = { __walkspeed_enabled = false, __walkspeed_value = 16, __jumppower_enabled = false, __jumppower_value = 50, __fov_enabled = false, __fov_value = 70, __spinbot_enabled = false, __spinbot_speed = 50, __cam_shake_disabled = false }
function System.player_mods.update_movement()
    local humanoid = LocalPlayer.Character and LocalPlayer.Character:FindFirstChildOfClass("Humanoid")
    if not humanoid then return end
    if System.player_mods.__walkspeed_enabled then humanoid.WalkSpeed = System.player_mods.__walkspeed_value end
    if System.player_mods.__jumppower_enabled then humanoid.UseJumpPower = true; humanoid.JumpPower = System.player_mods.__jumppower_value end
end
function System.player_mods.update_fov() Camera.FieldOfView = System.player_mods.__fov_enabled and System.player_mods.__fov_value or 70 end
function System.player_mods.spinbot_loop()
    local hrp = LocalPlayer.Character and LocalPlayer.Character:FindFirstChild("HumanoidRootPart")
    if hrp and System.player_mods.__spinbot_enabled then hrp.CFrame = hrp.CFrame * CFrame.Angles(0, math.rad(System.player_mods.__spinbot_speed), 0) end
end
function System.player_mods.disable_camera_shake(state)
    System.player_mods.__cam_shake_disabled = state
    if state then pcall(function() for _, scr in pairs(LocalPlayer:WaitForChild("PlayerScripts"):GetDescendants()) do if scr.Name:lower():find("shake") and scr:IsA("ModuleScript") then local s, m = pcall(require, scr); if s and type(m)=="table" and m.Shake then m.Shake = function() end end end end end) end
end

RunService.RenderStepped:Connect(function()
    System.visuals.update_esp()
    System.player_mods.update_fov()
    System.player_mods.spinbot_loop()
    if System.player_mods.__walkspeed_enabled or System.player_mods.__jumppower_enabled then System.player_mods.update_movement() end
end)

local Features = {}

Features.TargetPlayer = {
    Enabled = false,
    SelectedTarget = nil,
    PlayerMap = {}
}

function Features.TargetPlayer.updatePlayerList()
    table.clear(Features.TargetPlayer.PlayerMap)
    for _, player in pairs(Players:GetPlayers()) do
        if player ~= LocalPlayer then
            Features.TargetPlayer.PlayerMap[player.DisplayName] = player.Name
            Features.TargetPlayer.PlayerMap[player.Name] = player.Name
        end
    end
end

function Features.TargetPlayer.setTarget(playerName)
    if not playerName or playerName == "" or playerName == "None" then
        Features.TargetPlayer.SelectedTarget = nil
        getgenv().SelectedTarget = nil
        return
    end
    
    local lowerName = string.lower(playerName)
    for _, player in pairs(Players:GetPlayers()) do
        if player ~= LocalPlayer then
            if string.lower(player.Name):match(lowerName) or string.lower(player.DisplayName):match(lowerName) then
                Features.TargetPlayer.SelectedTarget = player.Name
                getgenv().SelectedTarget = player.Name
                notify('Target Player', 'Locked onto: ' .. player.Name, 3)
                return
            end
        end
    end
    
    Features.TargetPlayer.SelectedTarget = nil
    getgenv().SelectedTarget = nil
    notify('Target Player', 'Player not found', 3)
end

function Features.TargetPlayer.getTargetPlayer()
    if not Features.TargetPlayer.Enabled or not Features.TargetPlayer.SelectedTarget then return nil end
    return Players:FindFirstChild(Features.TargetPlayer.SelectedTarget)
end

Players.PlayerAdded:Connect(function() task.wait(0.5); Features.TargetPlayer.updatePlayerList() end)
Players.PlayerRemoving:Connect(function(player) 
    if Features.TargetPlayer.SelectedTarget == player.Name then
        Features.TargetPlayer.SelectedTarget = nil
    end
    task.wait(0.5); Features.TargetPlayer.updatePlayerList() 
end)

Features.TargetPlayer.updatePlayerList()

Features.AvatarChanger = {
    Enabled = false,
    _persistentTasks = {}
}

local function descriptions_match(a, b)
    if not a or not b then return false end
    local keys = {"Shirt", "Pants", "ShirtGraphic", "Head", "Face", "BodyTypeScale", "HeightScale"}
    for _, k in ipairs(keys) do
        if (a[k] ~= nil and b[k] ~= nil) and tostring(a[k]) ~= tostring(b[k]) then return false end
    end
    return true
end

local function force_apply_brutal(hum, desc)
    if not hum or not desc then return false end
    for _ = 1, 15 do
        pcall(function() hum:ApplyDescriptionClientServer(desc) end)
        task.wait(0.05)
        local applied = pcall(function() return hum:GetAppliedDescription() end)
        if applied and descriptions_match(applied, desc) then return true end
    end
    
    pcall(function() hum.Description = Instance.new("HumanoidDescription") end)
    task.wait(0.1)
    
    for _ = 1, 15 do
        pcall(function() hum:ApplyDescriptionClientServer(desc) end)
        task.wait(0.05)
        local applied = pcall(function() return hum:GetAppliedDescription() end)
        if applied and descriptions_match(applied, desc) then return true end
    end
    return false
end

function Features.AvatarChanger.setAvatar(targetName)
    local char = LocalPlayer.Character
    local hum = char and char:FindFirstChildOfClass("Humanoid")
    if not hum or targetName == "" then return end

    local success, desc = pcall(function()
        local id = Players:GetUserIdFromNameAsync(targetName)
        return Players:GetHumanoidDescriptionFromUserId(id)
    end)

    if success and desc then
        pcall(function()
            LocalPlayer:ClearCharacterAppearance()
            hum.Description = Instance.new("HumanoidDescription")
        end)
        task.wait(0.05)
        force_apply_brutal(hum, desc)
        task.wait(0.1)
        pcall(function()
            if LocalPlayer.Character then
                LocalPlayer.Character:BreakJoints()
            end
        end)
    end
end

Features.Visuals = {
    Rain = {
        Enabled = false,
        Particles = {},
        MaxParticles = 5000,
        SpawnArea = 500,
        FallSpeed = 25,
        SpawnHeight = 100,
        SpawnRate = 3,
        Color = Color3.fromRGB(100, 200, 255)
    },
    Plasma = {
        Enabled = false,
        Active = false,
        NumTrails = 8,
        TrailColor = Color3.fromRGB(0, 255, 255),
        Attachments = {},
        LastBall = nil
    }
}

local function get_ball()
    local balls = Workspace:FindFirstChild('Balls')
    if not balls then return nil end
    for _, ball in pairs(balls:GetChildren()) do
        if ball:GetAttribute('realBall') then
            return ball
        end
    end
    return nil
end

local ParticleFolder = Workspace:FindFirstChild("MagicalParticles") or Instance.new("Folder", Workspace)
ParticleFolder.Name = "MagicalParticles"

local function spawn_rain()
    local r = Features.Visuals.Rain
    if not r.Enabled or #r.Particles >= r.MaxParticles then return end
    
    local char = LocalPlayer.Character
    local pos = char and char.PrimaryPart and char.PrimaryPart.Position or Camera.CFrame.Position
    
    for _ = 1, r.SpawnRate do
        local particle = Instance.new("Part")
        particle.Size = Vector3.new(0.9, 0.9, 0.9)
        particle.Shape = Enum.PartType.Ball
        particle.Material = Enum.Material.Neon
        particle.Color = r.Color
        particle.CanCollide = false
        particle.Anchored = true
        particle.Position = Vector3.new(
            pos.X + math.random(-r.SpawnArea, r.SpawnArea),
            pos.Y + r.SpawnHeight,
            pos.Z + math.random(-r.SpawnArea, r.SpawnArea)
        )
        particle.Parent = ParticleFolder
        
        table.insert(r.Particles, {
            Part = particle,
            Velocity = Vector3.new(math.random(-2, 2), -r.FallSpeed, math.random(-2, 2)),
            TimeAlive = 0,
            FloatFreq = math.random(2, 4),
            FloatAmp = math.random(2, 5)
        })
    end
end

local function update_rain(delta)
    local r = Features.Visuals.Rain
    local char = LocalPlayer.Character
    local pos = char and char.PrimaryPart and char.PrimaryPart.Position or Camera.CFrame.Position
    
    for i = #r.Particles, 1, -1 do
        local p = r.Particles[i]
        if not p.Part or not p.Part.Parent then
            table.remove(r.Particles, i)
        else
            p.TimeAlive = p.TimeAlive + delta
            local float_x = math.sin(p.TimeAlive * p.FloatFreq) * p.FloatAmp * delta
            local float_z = math.cos(p.TimeAlive * p.FloatFreq) * p.FloatAmp * delta
            
            local new_pos = p.Part.Position + Vector3.new(
                p.Velocity.X * delta + float_x,
                p.Velocity.Y * delta,
                p.Velocity.Z * delta + float_z
            )
            p.Part.Position = new_pos
            
            if new_pos.Y < pos.Y - 20 or (new_pos - pos).Magnitude > r.SpawnArea * 1.5 then
                p.Part:Destroy()
                table.remove(r.Particles, i)
            end
        end
    end
end

local function create_plasma(ball)
    local p = Features.Visuals.Plasma
    if p.Active then return end
    p.Active = true
    p.Attachments = {}
    
    for i = 1, p.NumTrails do
        local angle = (i / p.NumTrails) * math.pi * 2
        local radius = math.random(150, 250) / 100
        local height = math.random(-150, 150) / 100
        
        local a0 = Instance.new("Attachment", ball)
        local a1 = Instance.new("Attachment", ball)
        
        local trail = Instance.new("Trail", ball)
        trail.Attachment0 = a0
        trail.Attachment1 = a1
        trail.Lifetime = 0.6
        trail.FaceCamera = true
        trail.LightEmission = 1
        trail.Color = ColorSequence.new(p.TrailColor)
        
        table.insert(p.Attachments, {
            a0 = a0, a1 = a1, trail = trail,
            baseAngle = angle, angle = 0, speed = math.random(15, 30) / 10,
            spiralSpeed = math.random(25, 45) / 10, baseRadius = radius, baseHeight = height
        })
    end
end

local function animate_plasma(delta)
    local p = Features.Visuals.Plasma
    if not p.Active then return end
    local time = tick()
    
    for _, t in ipairs(p.Attachments) do
        t.angle = t.angle + t.speed * delta
        local spiral = t.angle * t.spiralSpeed
        
        t.a0.Position = Vector3.new(
            math.cos(t.baseAngle + t.angle) * t.baseRadius,
            t.baseHeight + math.sin((t.baseAngle + t.angle) * 3) * 0.8,
            math.sin(t.baseAngle + t.angle) * t.baseRadius
        )
        
        t.a1.Position = Vector3.new(
            math.cos(t.baseAngle + t.angle + math.pi) * t.baseRadius,
            -t.baseHeight + math.cos((t.baseAngle + t.angle) * 2.5) * 0.8,
            math.sin(t.baseAngle + t.angle + math.pi) * t.baseRadius
        )
    end
end

local function cleanup_plasma(ball)
    if not ball then return end
    for _, obj in pairs(ball:GetChildren()) do
        if obj:IsA("Trail") or (obj:IsA("Attachment") and not obj.Name:match("Attachment")) then
            obj:Destroy()
        end
    end
    Features.Visuals.Plasma.Active = false
    Features.Visuals.Plasma.Attachments = {}
end

RunService.Heartbeat:Connect(function(delta)
    if Features.Visuals.Rain.Enabled then
        spawn_rain()
    end
    update_rain(delta)
    
    local ball = get_ball()
    local p = Features.Visuals.Plasma
    
    if p.Enabled then
        if ball and ball ~= p.LastBall then
            if p.LastBall then cleanup_plasma(p.LastBall) end
            create_plasma(ball)
            p.LastBall = ball
        end
        if ball and p.Active then animate_plasma(delta) end
    else
        if p.LastBall then
            cleanup_plasma(p.LastBall)
            p.LastBall = nil
        end
    end
end)



do
--[[
    LYRA UI REDESIGN V2 — AURORA MOCKUP
    UI only: no game feature logic is included.

    Direction:
      • Compact icon sidebar that expands on hover
      • Clean glass / premium visual language
      • Responsive desktop/mobile scaling
      • Logo used in the brand and minimized reopen button
      • Existing theme palettes retained

    Run as a LocalScript or in an environment that can parent a ScreenGui.
]]

if not game:IsLoaded() then game.Loaded:Wait() end

local Players = game:GetService("Players")
local TweenService = game:GetService("TweenService")
local UserInputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")
local GuiService = game:GetService("GuiService")

local LocalPlayer = Players.LocalPlayer
if not LocalPlayer then
    Players:GetPropertyChangedSignal("LocalPlayer"):Wait()
    LocalPlayer = Players.LocalPlayer
end

local LOGO = "rbxassetid://108975487405628"
local ICONS = {
    Home = "rbxassetid://6034509993",
    QB = "rbxassetid://6034684949",
    Catching = "rbxassetid://6034227067",
    Player = "rbxassetid://103807548355126",
    Physics = "rbxassetid://11537490966",
    Visual = "rbxassetid://88610077537468",
    Defense = "rbxassetid://14939023862",
    Automatics = "rbxassetid://6034502844",
    Trolling = "rbxassetid://6031097225",
    Misc = "rbxassetid://129082556946713",
    Settings = "rbxassetid://6031280882",
    Search = "rbxassetid://6031154871",
    Close = "rbxassetid://6031094678",
    Minimize = "rbxassetid://6031091004",
    Chevron = "rbxassetid://6031090990",
}

local Themes = {
    ["Aurora Gray"] = {
        MainBG = Color3.fromRGB(225, 228, 234), SidebarBG = Color3.fromRGB(216, 220, 227),
        TopbarBG = Color3.fromRGB(234, 237, 242), SectionBG = Color3.fromRGB(239, 242, 247),
        SectionHeaderBG = Color3.fromRGB(245, 247, 250), ElementBG = Color3.fromRGB(246, 248, 251),
        ElementHoverBG = Color3.fromRGB(252, 253, 255), Accent = Color3.fromRGB(138, 104, 238),
        TextMain = Color3.fromRGB(40, 45, 58), TextSub = Color3.fromRGB(103, 110, 126),
        Border = Color3.fromRGB(186, 193, 207), ControlBG = Color3.fromRGB(228, 232, 239),
    },
    ["Minimal White"] = {
        MainBG = Color3.fromRGB(247, 248, 250), SidebarBG = Color3.fromRGB(238, 241, 245),
        TopbarBG = Color3.fromRGB(243, 245, 248), SectionBG = Color3.fromRGB(250, 251, 253),
        SectionHeaderBG = Color3.fromRGB(245, 247, 250), ElementBG = Color3.fromRGB(255, 255, 255),
        ElementHoverBG = Color3.fromRGB(244, 247, 252), Accent = Color3.fromRGB(124, 92, 255),
        TextMain = Color3.fromRGB(36, 42, 56), TextSub = Color3.fromRGB(106, 114, 130),
        Border = Color3.fromRGB(212, 218, 228), ControlBG = Color3.fromRGB(236, 240, 246),
    },
    ["Midnight Violet"] = {
        MainBG = Color3.fromRGB(223, 214, 242), SidebarBG = Color3.fromRGB(214, 204, 235),
        TopbarBG = Color3.fromRGB(231, 223, 248), SectionBG = Color3.fromRGB(238, 231, 252),
        SectionHeaderBG = Color3.fromRGB(244, 238, 255), ElementBG = Color3.fromRGB(247, 242, 255),
        ElementHoverBG = Color3.fromRGB(252, 248, 255), Accent = Color3.fromRGB(138, 94, 232),
        TextMain = Color3.fromRGB(49, 39, 82), TextSub = Color3.fromRGB(103, 87, 140),
        Border = Color3.fromRGB(188, 171, 223), ControlBG = Color3.fromRGB(226, 217, 242),
    },
    ["Tokyo Night"] = {
        MainBG = Color3.fromRGB(52, 59, 84), SidebarBG = Color3.fromRGB(46, 53, 77),
        TopbarBG = Color3.fromRGB(58, 65, 92), SectionBG = Color3.fromRGB(67, 75, 105),
        SectionHeaderBG = Color3.fromRGB(74, 82, 116), ElementBG = Color3.fromRGB(82, 90, 125),
        ElementHoverBG = Color3.fromRGB(95, 104, 142), Accent = Color3.fromRGB(122, 162, 247),
        TextMain = Color3.fromRGB(233, 238, 255), TextSub = Color3.fromRGB(191, 198, 226),
        Border = Color3.fromRGB(110, 121, 162), ControlBG = Color3.fromRGB(60, 68, 97),
    },
    Dracula = {
        MainBG = Color3.fromRGB(74, 76, 98), SidebarBG = Color3.fromRGB(66, 68, 88),
        TopbarBG = Color3.fromRGB(82, 84, 108), SectionBG = Color3.fromRGB(92, 94, 121),
        SectionHeaderBG = Color3.fromRGB(100, 102, 130), ElementBG = Color3.fromRGB(108, 110, 140),
        ElementHoverBG = Color3.fromRGB(122, 124, 156), Accent = Color3.fromRGB(189, 147, 249),
        TextMain = Color3.fromRGB(248, 248, 242), TextSub = Color3.fromRGB(220, 222, 233),
        Border = Color3.fromRGB(136, 139, 170), ControlBG = Color3.fromRGB(80, 82, 105),
    },
    Catppuccin = {
        MainBG = Color3.fromRGB(239, 241, 245), SidebarBG = Color3.fromRGB(230, 233, 239),
        TopbarBG = Color3.fromRGB(245, 247, 250), SectionBG = Color3.fromRGB(248, 250, 252),
        SectionHeaderBG = Color3.fromRGB(241, 244, 249), ElementBG = Color3.fromRGB(255, 255, 255),
        ElementHoverBG = Color3.fromRGB(245, 248, 252), Accent = Color3.fromRGB(136, 57, 239),
        TextMain = Color3.fromRGB(76, 79, 105), TextSub = Color3.fromRGB(108, 111, 133),
        Border = Color3.fromRGB(203, 208, 218), ControlBG = Color3.fromRGB(232, 235, 243),
    },
    ["Rose Pine"] = {
        MainBG = Color3.fromRGB(244, 238, 235), SidebarBG = Color3.fromRGB(236, 229, 225),
        TopbarBG = Color3.fromRGB(249, 244, 241), SectionBG = Color3.fromRGB(252, 248, 246),
        SectionHeaderBG = Color3.fromRGB(244, 238, 235), ElementBG = Color3.fromRGB(255, 251, 249),
        ElementHoverBG = Color3.fromRGB(248, 242, 239), Accent = Color3.fromRGB(170, 122, 171),
        TextMain = Color3.fromRGB(83, 68, 90), TextSub = Color3.fromRGB(122, 106, 126),
        Border = Color3.fromRGB(216, 201, 196), ControlBG = Color3.fromRGB(239, 232, 228),
    },
    ["Hello Kitty"] = {
        MainBG = Color3.fromRGB(255, 231, 239), SidebarBG = Color3.fromRGB(255, 218, 230),
        TopbarBG = Color3.fromRGB(255, 224, 236), SectionBG = Color3.fromRGB(255, 240, 246),
        SectionHeaderBG = Color3.fromRGB(255, 226, 238), ElementBG = Color3.fromRGB(255, 248, 251),
        ElementHoverBG = Color3.fromRGB(255, 229, 240), Accent = Color3.fromRGB(235, 70, 105),
        TextMain = Color3.fromRGB(88, 36, 53), TextSub = Color3.fromRGB(154, 84, 105),
        Border = Color3.fromRGB(245, 156, 181), ControlBG = Color3.fromRGB(255, 236, 244),
    },
}

-- Extra colors only extend the original palettes; none of the original colors are replaced.
local ThemeAccents = {
    ["Aurora Gray"] = {Accent2 = Color3.fromRGB(201, 136, 255), Accent3 = Color3.fromRGB(111, 156, 255)},
    ["Minimal White"] = {Accent2 = Color3.fromRGB(255, 138, 188), Accent3 = Color3.fromRGB(120, 164, 255)},
    ["Midnight Violet"] = {Accent2 = Color3.fromRGB(226, 132, 214), Accent3 = Color3.fromRGB(126, 143, 245)},
    ["Tokyo Night"] = {Accent2 = Color3.fromRGB(125, 207, 255), Accent3 = Color3.fromRGB(187, 154, 247)},
    Dracula = {Accent2 = Color3.fromRGB(255, 121, 198), Accent3 = Color3.fromRGB(139, 233, 253)},
    Catppuccin = {Accent2 = Color3.fromRGB(244, 184, 228), Accent3 = Color3.fromRGB(114, 135, 253)},
    ["Rose Pine"] = {Accent2 = Color3.fromRGB(230, 170, 112), Accent3 = Color3.fromRGB(144, 122, 169)},
    ["Hello Kitty"] = {Accent2 = Color3.fromRGB(255, 148, 188), Accent3 = Color3.fromRGB(196, 102, 255)},
}

for name, extra in pairs(ThemeAccents) do
    Themes[name].Accent2 = extra.Accent2
    Themes[name].Accent3 = extra.Accent3
end

local themeName = "Midnight Violet"
local T = Themes[themeName]
local themeBindings = {}
local connections = {}
local destroyed = false
local uiScaleFactor = 0.84
local notificationsEnabled = true
local sidebarHoverEnabled = true
local menuToggleKey = Enum.KeyCode.RightControl
local GetTargetScale
local SetSidebar

local function DetectExecutor()
    local probes = {
        function() if identifyexecutor then return identifyexecutor() end end,
        function() if getexecutorname then return getexecutorname() end end,
        function() if getexecutor then return getexecutor() end end,
        function() if syn and syn.getexecutor then return syn.getexecutor() end end,
    }
    for _, probe in ipairs(probes) do
        local ok, value = pcall(probe)
        if ok and value and tostring(value) ~= "" then
            return tostring(value)
        end
    end
    return "Unknown"
end

local executorName = DetectExecutor()

local function New(className, props)
    local object = Instance.new(className)
    for property, value in pairs(props or {}) do
        if property ~= "Parent" then object[property] = value end
    end
    if props and props.Parent then object.Parent = props.Parent end
    return object
end

local function Corner(parent, radius)
    return New("UICorner", {Parent = parent, CornerRadius = UDim.new(0, radius or 10)})
end

local function Stroke(parent, colorKey, transparency, thickness)
    local stroke = New("UIStroke", {
        Parent = parent, Color = T[colorKey or "Border"],
        Transparency = transparency or 0, Thickness = thickness or 1,
        ApplyStrokeMode = Enum.ApplyStrokeMode.Border,
    })
    table.insert(themeBindings, function()
        if stroke.Parent then stroke.Color = T[colorKey or "Border"] end
    end)
    return stroke
end

local function BindTheme(callback)
    table.insert(themeBindings, callback)
    callback()
end

local function Mix(a, b, alpha)
    return a:Lerp(b, alpha)
end

local function Gradient(parent, keys, rotation, transparency)
    local gradient = New("UIGradient", {
        Parent = parent,
        Rotation = rotation or 0,
        Color = ColorSequence.new(Color3.new(1, 1, 1)),
        Transparency = transparency or NumberSequence.new(0),
    })
    BindTheme(function()
        if not gradient.Parent then return end
        local points = {}
        for index, key in ipairs(keys) do
            local position = (#keys == 1) and 0 or ((index - 1) / (#keys - 1))
            local color
            if type(key) == "string" then color = T[key]
            else color = key(T) end
            points[index] = ColorSequenceKeypoint.new(position, color)
        end
        gradient.Color = ColorSequence.new(points)
    end)
    return gradient
end

local function Tween(object, duration, properties, style, direction)
    if not object or not object.Parent then return end
    local tween = TweenService:Create(object, TweenInfo.new(
        duration or 0.2, style or Enum.EasingStyle.Quart,
        direction or Enum.EasingDirection.Out
    ), properties)
    tween:Play()
    return tween
end

local parent
pcall(function()
    parent = (gethui and gethui()) or game:GetService("CoreGui")
end)
if not parent then parent = LocalPlayer:WaitForChild("PlayerGui") end

for _, location in ipairs({parent, LocalPlayer:FindFirstChild("PlayerGui")}) do
    if location then
        local old = location:FindFirstChild("LyraRedesignMockup")
        if old then old:Destroy() end
    end
end

local Screen = New("ScreenGui", {
    Parent = parent, Name = "LyraRedesignMockup", ResetOnSpawn = false,
    IgnoreGuiInset = false, ZIndexBehavior = Enum.ZIndexBehavior.Sibling,
})

local Dim = New("Frame", {
    Parent = Screen, Name = "Dim", Size = UDim2.fromScale(1, 1),
    BackgroundColor3 = T.MainBG, BackgroundTransparency = 1,
    BorderSizePixel = 0,
})

local Window = New("CanvasGroup", {
    Parent = Screen, Name = "Window", AnchorPoint = Vector2.new(0.5, 0.5),
    Position = UDim2.fromScale(0.5, 0.5), Size = UDim2.fromOffset(900, 540),
    BackgroundColor3 = T.MainBG, BorderSizePixel = 0, ClipsDescendants = true,
})
Corner(Window, 18)
local windowStroke = Stroke(Window, "Border", 0.12, 1.2)
local Scale = New("UIScale", {Parent = Window, Scale = uiScaleFactor})
Gradient(Window, {
    function(t) return Mix(t.MainBG, t.Accent3, 0.10) end,
    "MainBG",
    function(t) return Mix(t.MainBG, t.Accent2, 0.11) end,
}, 22)

-- Theme-colored aurora fields. They overlap to mix all three accent colors.
local AuroraLayer = New("Frame", {
    Parent = Window, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
    ClipsDescendants = true, ZIndex = 0,
})
local function AuroraBlob(position, size, colorKey, rotation)
    local blob = New("Frame", {
        Parent = AuroraLayer, AnchorPoint = Vector2.new(0.5, 0.5), Position = position,
        Size = size, BackgroundColor3 = T[colorKey], BackgroundTransparency = 0.88,
        BorderSizePixel = 0, ZIndex = 0,
    })
    Corner(blob, 999)
    local fade = New("UIGradient", {
        Parent = blob, Rotation = rotation or 0,
        Transparency = NumberSequence.new({
            NumberSequenceKeypoint.new(0, 0.28),
            NumberSequenceKeypoint.new(0.48, 0.63),
            NumberSequenceKeypoint.new(1, 1),
        }),
    })
    BindTheme(function()
        if blob.Parent then blob.BackgroundColor3 = T[colorKey] end
    end)
    return blob
end
local AuroraA = AuroraBlob(UDim2.fromScale(0.13, 0.03), UDim2.fromOffset(500, 330), "Accent", 28)
local AuroraB = AuroraBlob(UDim2.fromScale(0.88, 0.12), UDim2.fromOffset(470, 300), "Accent2", 145)
local AuroraC = AuroraBlob(UDim2.fromScale(0.70, 0.96), UDim2.fromOffset(610, 330), "Accent3", -20)

-- Ambient premium glow, clipped inside the window.
local GlowOne = New("Frame", {
    Parent = Window, AnchorPoint = Vector2.new(0.5, 0.5), Position = UDim2.fromScale(0.17, 0.06),
    Size = UDim2.fromOffset(420, 260), BackgroundColor3 = T.Accent,
    BackgroundTransparency = 0.95, BorderSizePixel = 0, ZIndex = 0,
})
Corner(GlowOne, 200)
local glowGradient = New("UIGradient", {
    Parent = GlowOne,
    Transparency = NumberSequence.new({
        NumberSequenceKeypoint.new(0, 0.15), NumberSequenceKeypoint.new(1, 1),
    }), Rotation = 35,
})

local SIDEBAR_COMPACT, SIDEBAR_OPEN = 64, 188
local sidebarExpanded = false
local currentPage = "Home"
local navEntries = {}
local pages = {}

local Sidebar = New("Frame", {
    Parent = Window, Name = "Sidebar", Size = UDim2.new(0, SIDEBAR_COMPACT, 1, 0),
    BackgroundColor3 = T.SidebarBG, BackgroundTransparency = 0.08,
    BorderSizePixel = 0, ZIndex = 20, ClipsDescendants = true,
})
Gradient(Sidebar, {
    function(t) return Mix(t.SidebarBG, t.Accent, 0.16) end,
    "SidebarBG",
    function(t) return Mix(t.SidebarBG, t.Accent3, 0.13) end,
}, 90)
local sideLine = New("Frame", {
    Parent = Sidebar, AnchorPoint = Vector2.new(1, 0), Position = UDim2.fromScale(1, 0),
    Size = UDim2.new(0, 1, 1, 0), BackgroundColor3 = T.Border,
    BackgroundTransparency = 0.35, BorderSizePixel = 0, ZIndex = 21,
})

local Brand = New("Frame", {
    Parent = Sidebar, Size = UDim2.new(1, 0, 0, 78), BackgroundTransparency = 1, ZIndex = 22,
})
local BrandLogoShell = New("Frame", {
    Parent = Brand, Position = UDim2.fromOffset(17, 17), Size = UDim2.fromOffset(42, 42),
    BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.1, ZIndex = 23,
})
Corner(BrandLogoShell, 12)
Gradient(BrandLogoShell, {"Accent", "Accent2", "Accent3"}, 35)
Stroke(BrandLogoShell, "Accent2", 0.08, 1.2)
local BrandLogo = New("ImageLabel", {
    Parent = BrandLogoShell, Position = UDim2.fromOffset(5, 5), Size = UDim2.new(1, -10, 1, -10),
    BackgroundTransparency = 1, Image = LOGO, ScaleType = Enum.ScaleType.Fit, ZIndex = 24,
})
local BrandTitle = New("TextLabel", {
    Parent = Brand, Position = UDim2.fromOffset(72, 18), Size = UDim2.new(1, -84, 0, 22),
    BackgroundTransparency = 1, Text = "LYRA AURORA", Font = Enum.Font.GothamBold,
    TextSize = 16, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, TextTruncate = Enum.TextTruncate.AtEnd, ZIndex = 23,
})
local BrandSub = New("TextLabel", {
    Parent = Brand, Position = UDim2.fromOffset(72, 40), Size = UDim2.new(1, -84, 0, 16),
    BackgroundTransparency = 1, Text = "MULTI-TONE INTERFACE", Font = Enum.Font.GothamMedium,
    TextSize = 8, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, TextTruncate = Enum.TextTruncate.AtEnd, ZIndex = 23,
})

local NavScroll = New("ScrollingFrame", {
    Parent = Sidebar, Position = UDim2.fromOffset(0, 78), Size = UDim2.new(1, 0, 1, -150),
    BackgroundTransparency = 1, BorderSizePixel = 0, ScrollBarThickness = 0,
    CanvasSize = UDim2.new(), AutomaticCanvasSize = Enum.AutomaticSize.Y, ZIndex = 22,
})
New("UIPadding", {
    Parent = NavScroll, PaddingLeft = UDim.new(0, 10), PaddingRight = UDim.new(0, 10),
    PaddingTop = UDim.new(0, 4), PaddingBottom = UDim.new(0, 8),
})
New("UIListLayout", {Parent = NavScroll, Padding = UDim.new(0, 5), SortOrder = Enum.SortOrder.LayoutOrder})

local SideFooter = New("Frame", {
    Parent = Sidebar, AnchorPoint = Vector2.new(0, 1), Position = UDim2.fromScale(0, 1),
    Size = UDim2.new(1, 0, 0, 70), BackgroundTransparency = 1, ZIndex = 22,
})
local Profile = New("Frame", {
    Parent = SideFooter, Position = UDim2.fromOffset(10, 8), Size = UDim2.new(1, -20, 0, 50),
    BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.25, ZIndex = 23,
})
Corner(Profile, 12)
Gradient(Profile, {function(t) return Mix(t.ElementBG, t.Accent3, .13) end, function(t) return Mix(t.ElementBG, t.Accent2, .10) end}, 15)
local Avatar = New("ImageLabel", {
    Parent = Profile, Position = UDim2.fromOffset(8, 8), Size = UDim2.fromOffset(34, 34),
    BackgroundColor3 = T.ControlBG, Image = "", ZIndex = 24,
})
Corner(Avatar, 10)
local ProfileName = New("TextLabel", {
    Parent = Profile, Position = UDim2.fromOffset(50, 7), Size = UDim2.new(1, -56, 0, 19),
    BackgroundTransparency = 1, Text = LocalPlayer.DisplayName, Font = Enum.Font.GothamSemibold,
    TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, TextTruncate = Enum.TextTruncate.AtEnd, ZIndex = 24,
})
local ProfileStatus = New("TextLabel", {
    Parent = Profile, Position = UDim2.fromOffset(50, 25), Size = UDim2.new(1, -56, 0, 16),
    BackgroundTransparency = 1, Text = "●  BLADE BALL", Font = Enum.Font.GothamMedium,
    TextSize = 8, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Left,
    TextTransparency = 1, ZIndex = 24,
})
task.spawn(function()
    local ok, image = pcall(function()
        return Players:GetUserThumbnailAsync(LocalPlayer.UserId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size100x100)
    end)
    if ok and Avatar.Parent then Avatar.Image = image end
end)

local CONTENT_RIGHT_INSET = 8
local Content = New("Frame", {
    Parent = Window, Name = "Content", Position = UDim2.fromOffset(SIDEBAR_COMPACT, 0),
    Size = UDim2.new(1, -(SIDEBAR_COMPACT + CONTENT_RIGHT_INSET), 1, 0), BackgroundTransparency = 1, ZIndex = 5,
})

local function ApplyContentLayout(expanded, instant)
    local offset = expanded and SIDEBAR_OPEN or SIDEBAR_COMPACT
    if instant then
        Content.Position = UDim2.fromOffset(offset, 0)
        Content.Size = UDim2.new(1, -(offset + CONTENT_RIGHT_INSET), 1, 0)
    else
        Tween(Content, 0.28, {
            Position = UDim2.fromOffset(offset, 0),
            Size = UDim2.new(1, -(offset + CONTENT_RIGHT_INSET), 1, 0),
        })
    end
end

local Topbar = New("Frame", {
    Parent = Content, Size = UDim2.new(1, 0, 0, 72), BackgroundColor3 = T.TopbarBG,
    BackgroundTransparency = 0.18, BorderSizePixel = 0, ZIndex = 10,
})
Gradient(Topbar, {
    function(t) return Mix(t.TopbarBG, t.Accent3, 0.16) end,
    "TopbarBG",
    function(t) return Mix(t.TopbarBG, t.Accent2, 0.14) end,
}, 0)
local topLine = New("Frame", {
    Parent = Topbar, AnchorPoint = Vector2.new(0, 1), Position = UDim2.fromScale(0, 1),
    Size = UDim2.new(1, 0, 0, 1), BackgroundColor3 = T.Border,
    BackgroundTransparency = 0.45, BorderSizePixel = 0, ZIndex = 11,
})
local PageTitle = New("TextLabel", {
    Parent = Topbar, Position = UDim2.fromOffset(24, 14), Size = UDim2.fromOffset(280, 24),
    BackgroundTransparency = 1, Text = "Status", Font = Enum.Font.GothamBold,
    TextSize = 19, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 12,
})
local Breadcrumb = New("TextLabel", {
    Parent = Topbar, Position = UDim2.fromOffset(24, 39), Size = UDim2.fromOffset(360, 16),
    BackgroundTransparency = 1, Text = "Lyra  /  Status", Font = Enum.Font.Gotham,
    TextSize = 10, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 12,
})

local SearchBox = New("Frame", {
    Parent = Topbar, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -104, 0.5, 0),
    Size = UDim2.fromOffset(210, 36), BackgroundColor3 = T.ControlBG,
    BackgroundTransparency = 0.12, ZIndex = 12,
})
Corner(SearchBox, 11)
Gradient(SearchBox, {function(t) return Mix(t.ControlBG, t.Accent3, .12) end, function(t) return Mix(t.ControlBG, t.Accent2, .10) end}, 0)
Stroke(SearchBox, "Border", 0.2)
local SearchIcon = New("ImageLabel", {
    Parent = SearchBox, Position = UDim2.fromOffset(11, 9), Size = UDim2.fromOffset(18, 18),
    BackgroundTransparency = 1, Image = ICONS.Search, ImageColor3 = T.TextSub, ZIndex = 13,
})
local SearchInput = New("TextBox", {
    Parent = SearchBox, Position = UDim2.fromOffset(38, 0), Size = UDim2.new(1, -46, 1, 0),
    BackgroundTransparency = 1, Text = "", PlaceholderText = "Search this page...",
    PlaceholderColor3 = T.TextSub, TextColor3 = T.TextMain, Font = Enum.Font.Gotham,
    TextSize = 11, TextXAlignment = Enum.TextXAlignment.Left, ClearTextOnFocus = false, ZIndex = 13,
})

local function IconButton(icon, xOffset)
    local button = New("ImageButton", {
        Parent = Topbar, AnchorPoint = Vector2.new(1, 0.5),
        Position = UDim2.new(1, xOffset, 0.5, 0), Size = UDim2.fromOffset(32, 32),
        BackgroundColor3 = T.ControlBG, BackgroundTransparency = 0.18,
        Image = icon, ImageColor3 = T.TextSub, AutoButtonColor = false, ZIndex = 12,
    })
    Corner(button, 10)
    button.MouseEnter:Connect(function()
        Tween(button, 0.16, {BackgroundTransparency = 0, ImageColor3 = T.TextMain})
    end)
    button.MouseLeave:Connect(function()
        Tween(button, 0.16, {BackgroundTransparency = 0.18, ImageColor3 = T.TextSub})
    end)
    return button
end
local MinimizeButton = IconButton(ICONS.Minimize, -58)
local CloseButton = IconButton(ICONS.Close, -18)

local PageHost = New("Frame", {
    Parent = Content, Position = UDim2.fromOffset(0, 72), Size = UDim2.new(1, 0, 1, -72),
    BackgroundTransparency = 1, ClipsDescendants = true, ZIndex = 6,
})

local OpenButton = New("ImageButton", {
    Parent = Screen, Name = "OpenButton", AnchorPoint = Vector2.new(0, 0.5),
    Position = UDim2.new(0, 20, 0.5, 0), Size = UDim2.fromOffset(54, 54),
    BackgroundColor3 = T.MainBG, BackgroundTransparency = 0.04,
    Image = LOGO, ScaleType = Enum.ScaleType.Fit, Visible = false,
    AutoButtonColor = false, ZIndex = 100,
})
Corner(OpenButton, 16)
Gradient(OpenButton, {"Accent", "Accent2", "Accent3"}, 35)
Stroke(OpenButton, "Accent2", 0.02, 1.6)
New("UIPadding", {
    Parent = OpenButton, PaddingTop = UDim.new(0, 8), PaddingBottom = UDim.new(0, 8),
    PaddingLeft = UDim.new(0, 8), PaddingRight = UDim.new(0, 8),
})

local DropdownLayer = New("Frame", {
    Parent = Window, Name = "DropdownLayer", Size = UDim2.fromScale(1, 1),
    BackgroundTransparency = 1, BorderSizePixel = 0, ZIndex = 350, Visible = false,
})

-- Notification preview -------------------------------------------------------
local ToastHost = New("Frame", {
    Parent = Screen, AnchorPoint = Vector2.new(1, 1), Position = UDim2.new(1, -18, 1, -18),
    Size = UDim2.fromOffset(300, 300), BackgroundTransparency = 1, ZIndex = 200,
})
New("UIListLayout", {
    Parent = ToastHost, VerticalAlignment = Enum.VerticalAlignment.Bottom,
    HorizontalAlignment = Enum.HorizontalAlignment.Right, Padding = UDim.new(0, 8),
})
local function Toast(title, message, duration)
    local holder = New("Frame", {
        Parent = ToastHost, Size = UDim2.fromOffset(280, 0), BackgroundTransparency = 1,
        ClipsDescendants = true, ZIndex = 201,
    })
    local card = New("CanvasGroup", {
        Parent = holder, Position = UDim2.fromOffset(300, 0), Size = UDim2.fromOffset(280, 76),
        BackgroundColor3 = T.SectionBG, BackgroundTransparency = 0.06,
        GroupTransparency = 1, ZIndex = 202,
    })
    Corner(card, 13)
    Stroke(card, "Border", 0.16)
    local bar = New("Frame", {
        Parent = card, Size = UDim2.fromOffset(3, 76), BackgroundColor3 = T.Accent,
        BorderSizePixel = 0, ZIndex = 203,
    })
    Corner(bar, 3)
    New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(16, 12), Size = UDim2.new(1, -28, 0, 18),
        BackgroundTransparency = 1, Text = title, Font = Enum.Font.GothamBold,
        TextSize = 12, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left, ZIndex = 203,
    })
    New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(16, 33), Size = UDim2.new(1, -28, 0, 32),
        BackgroundTransparency = 1, Text = message, Font = Enum.Font.Gotham,
        TextSize = 10, TextColor3 = T.TextSub, TextWrapped = true,
        TextXAlignment = Enum.TextXAlignment.Left, TextYAlignment = Enum.TextYAlignment.Top, ZIndex = 203,
    })
    Tween(holder, 0.22, {Size = UDim2.fromOffset(280, 76)})
    Tween(card, 0.35, {Position = UDim2.fromOffset(0, 0), GroupTransparency = 0})
    task.delay(duration or 3.2, function()
        if not card.Parent then return end
        local out = Tween(card, 0.28, {Position = UDim2.fromOffset(300, 0), GroupTransparency = 1})
        if out then out.Completed:Wait() end
        if holder.Parent then holder:Destroy() end
    end)
end

notify = function(title, message, duration)
    if notificationsEnabled then
        Toast(title, message, duration)
    end
end

-- UI component factory ------------------------------------------------------

-- ============================================================================
-- Lyra ConfigManager (per-script root)
-- Root is derived only from SCRIPT_ID — never shared across scripts.
--   Lyra/<SCRIPT_ID>/Configs/*.json
--   Lyra/<SCRIPT_ID>/Autoload.txt
-- ============================================================================
local SCRIPT_ID = "Blade Ball"
local ConfigManager = (function()
    local HttpService = game:GetService("HttpService")
    local CM = {
        ScriptId = SCRIPT_ID,
        Root = "Lyra/" .. SCRIPT_ID,
        Folder = "Lyra/" .. SCRIPT_ID .. "/Configs",
        AutoloadFile = "Lyra/" .. SCRIPT_ID .. "/Autoload.txt",
        Flags = {},
        Ignore = { ["Config Name"] = true, ["View Configs"] = true, ["Autoload"] = true },
    }

    local function ensureTree()
        if typeof(makefolder) ~= "function" or typeof(isfolder) ~= "function" then return end
        if not isfolder("Lyra") then pcall(makefolder, "Lyra") end
        if not isfolder(CM.Root) then pcall(makefolder, CM.Root) end
        if not isfolder(CM.Folder) then pcall(makefolder, CM.Folder) end
    end

    local function sanitize(name)
        name = tostring(name or ""):gsub("^%s+", ""):gsub("%s+$", "")
        name = name:gsub("[\\/:*?\"<>|]", "_")
        return (name ~= "" and name) or "default"
    end

    local function encode(value)
        local t = typeof(value)
        if t == "Color3" then
            return { Type = "Color3", R = value.R, G = value.G, B = value.B }
        elseif t == "EnumItem" then
            return { Type = "EnumItem", EnumType = tostring(value.EnumType), Name = value.Name }
        elseif t == "table" then
            local out = {}
            for k, v in pairs(value) do out[k] = encode(v) end
            return out
        end
        return value
    end

    local function decode(value)
        if type(value) ~= "table" then return value end
        if value.Type == "Color3" then
            return Color3.new(value.R or 0, value.G or 0, value.B or 0)
        elseif value.Type == "EnumItem" then
            local et = Enum[value.EnumType]
            if et then
                local ok, item = pcall(function() return et[value.Name] end)
                if ok then return item end
            end
            return nil
        end
        local out = {}
        for k, v in pairs(value) do out[k] = decode(v) end
        return out
    end

    function CM.Register(name, control)
        if type(name) == "string" and type(control) == "table" then
            CM.Flags[name] = control
        end
        return control
    end

    function CM.List()
        local names = {}
        if typeof(listfiles) ~= "function" then return names end
        ensureTree()
        local ok, files = pcall(listfiles, CM.Folder)
        if not ok or type(files) ~= "table" then return names end
        for _, entry in ipairs(files) do
            local path = entry
            if type(entry) == "table" then
                path = entry.Path or entry.path or entry.Name or entry.name or tostring(entry)
            end
            if type(path) == "string" then
                local n = path:match("([^/\\]+)%.json$")
                if n then table.insert(names, n) end
            end
        end
        table.sort(names)
        return names
    end

    function CM.Save(name)
        if typeof(writefile) ~= "function" then return false, "no writefile" end
        ensureTree()
        name = sanitize(name)
        local data = {}
        for flagName, flag in pairs(CM.Flags) do
            if not CM.Ignore[flagName] and type(flag) == "table" and type(flag.Get) == "function" then
                local ok, value = pcall(flag.Get)
                if ok then data[flagName] = encode(value) end
            end
        end
        local ok, encoded = pcall(function() return HttpService:JSONEncode(data) end)
        if not ok then return false, encoded end
        local path = CM.Folder .. "/" .. name .. ".json"
        local wok, err = pcall(writefile, path, encoded)
        return wok, wok and path or err
    end

    function CM.Load(name)
        if typeof(readfile) ~= "function" or typeof(isfile) ~= "function" then
            return false, "no filesystem"
        end
        ensureTree()
        name = sanitize(name)
        local path = CM.Folder .. "/" .. name .. ".json"
        if not isfile(path) then return false, "not found" end
        local ok, raw = pcall(readfile, path)
        if not ok then return false, raw end
        local dok, data = pcall(function() return HttpService:JSONDecode(raw) end)
        if not dok or type(data) ~= "table" then return false, "bad json" end

        if data["UI Theme"] and CM.Flags["UI Theme"] then
            local decoded = decode(data["UI Theme"])
            pcall(function() CM.Flags["UI Theme"].Set(decoded, true) end)
        end

        local applied = 0
        for flagName, rawValue in pairs(data) do
            if not CM.Ignore[flagName] and flagName ~= "UI Theme" then
                local flag = CM.Flags[flagName]
                if flag and type(flag.Set) == "function" then
                    local value = decode(rawValue)
                    if pcall(function() flag.Set(value, true) end) then
                        applied = applied + 1
                    end
                end
            end
        end
        return true, applied
    end

    function CM.Delete(name)
        if typeof(delfile) ~= "function" or typeof(isfile) ~= "function" then
            return false, "no filesystem"
        end
        name = sanitize(name)
        local path = CM.Folder .. "/" .. name .. ".json"
        if isfile(path) then
            local ok, err = pcall(delfile, path)
            return ok, err
        end
        return false, "not found"
    end

    function CM.SetAutoload(name, enabled)
        if typeof(writefile) ~= "function" then return false end
        ensureTree()
        if enabled then
            pcall(writefile, CM.AutoloadFile, sanitize(name))
        elseif typeof(isfile) == "function" and isfile(CM.AutoloadFile) and typeof(delfile) == "function" then
            pcall(delfile, CM.AutoloadFile)
        end
        return true
    end

    function CM.GetAutoload()
        if typeof(readfile) ~= "function" or typeof(isfile) ~= "function" then return nil end
        if isfile(CM.AutoloadFile) then
            local ok, name = pcall(readfile, CM.AutoloadFile)
            if ok and type(name) == "string" and name ~= "" then
                return sanitize(name)
            end
        end
        return nil
    end

    function CM.LoadAutoload()
        local name = CM.GetAutoload()
        if not name then return false, "none" end
        return CM.Load(name)
    end

    ensureTree()
    return CM
end)()

local function CreatePage(name)
    local page = New("CanvasGroup", {
        Parent = PageHost, Name = name .. "Page", Size = UDim2.fromScale(1, 1),
        BackgroundTransparency = 1, Visible = false, GroupTransparency = 1,
    })
    local scroll = New("ScrollingFrame", {
        Parent = page, Size = UDim2.new(1, -10, 1, 0), BackgroundTransparency = 1,
        BorderSizePixel = 0, ScrollBarThickness = 6, ScrollBarImageColor3 = T.Accent,
        Active = true, ScrollingEnabled = true, ScrollingDirection = Enum.ScrollingDirection.Y,
        AutomaticCanvasSize = Enum.AutomaticSize.Y,
        -- CanvasSize is handled automatically via AutomaticCanvasSize
        CanvasSize = UDim2.fromOffset(0, 0),
        ScrollBarImageTransparency = 0.4,
    })
    New("UIPadding", {
        Parent = scroll, PaddingTop = UDim.new(0, 18), PaddingBottom = UDim.new(0, 24),
        PaddingLeft = UDim.new(0, 18), PaddingRight = UDim.new(0, 22),
    })
    -- UIListLayout goes DIRECTLY in the ScrollingFrame (no intermediate Frame wrapper)
    local bodyLayout = New("UIListLayout", {Parent = scroll, Padding = UDim.new(0, 14), SortOrder = Enum.SortOrder.LayoutOrder})
    -- The ScrollingFrame itself IS the body. Sections are parented to scroll.
    -- AutomaticCanvasSize = Y will track content automatically.
    pages[name] = {Group = page, Scroll = scroll, Body = scroll, BodyLayout = bodyLayout, Searchables = {}}
    BindTheme(function()
        if scroll.Parent then scroll.ScrollBarImageColor3 = T.Accent end
    end)
    return pages[name]
end

local function Section(page, title, subtitle, widthScale)
    local card = New("Frame", {
        Parent = page.Body, Size = UDim2.new(widthScale or 1, 0, 0, 0),
        AutomaticSize = Enum.AutomaticSize.Y, BackgroundColor3 = T.SectionBG,
        BackgroundTransparency = 0.08,
    })
    Corner(card, 15)
    Gradient(card, {
        function(t) return Mix(t.SectionBG, t.Accent3, 0.10) end,
        "SectionBG",
        function(t) return Mix(t.SectionBG, t.Accent2, 0.09) end,
    }, 18)
    local cardStroke = Stroke(card, "Border", 0.16, 1.1)
    Gradient(cardStroke, {"Accent3", "Border", "Accent2"}, 0, NumberSequence.new({
        NumberSequenceKeypoint.new(0, .18), NumberSequenceKeypoint.new(.5, .58), NumberSequenceKeypoint.new(1, .18)
    }))
    New("UIPadding", {
        Parent = card, PaddingTop = UDim.new(0, 14), PaddingBottom = UDim.new(0, 14),
        PaddingLeft = UDim.new(0, 14), PaddingRight = UDim.new(0, 14),
    })
    New("UIListLayout", {Parent = card, Padding = UDim.new(0, 9), SortOrder = Enum.SortOrder.LayoutOrder})
    local heading = New("Frame", {
        Parent = card, Size = UDim2.new(1, 0, 0, subtitle and 42 or 25), BackgroundTransparency = 1,
    })
    local accent = New("Frame", {
        Parent = heading, Position = UDim2.fromOffset(0, 2), Size = UDim2.fromOffset(3, 19),
        BackgroundColor3 = T.Accent, BorderSizePixel = 0,
    })
    Corner(accent, 3)
    Gradient(accent, {"Accent", "Accent2"}, 90)
    local titleLabel = New("TextLabel", {
        Parent = heading, Position = UDim2.fromOffset(12, 0), Size = UDim2.new(1, -12, 0, 22),
        BackgroundTransparency = 1, Text = title, Font = Enum.Font.GothamSemibold,
        TextSize = 13, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local subLabel
    if subtitle then
        subLabel = New("TextLabel", {
            Parent = heading, Position = UDim2.fromOffset(12, 22), Size = UDim2.new(1, -12, 0, 17),
            BackgroundTransparency = 1, Text = subtitle, Font = Enum.Font.Gotham,
            TextSize = 9, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
        })
    end
    BindTheme(function()
        if card.Parent then
            card.BackgroundColor3 = T.SectionBG; accent.BackgroundColor3 = T.Accent
            titleLabel.TextColor3 = T.TextMain
            if subLabel then subLabel.TextColor3 = T.TextSub end
        end
    end)
    return card
end

local function ElementBase(page, parent, label, height)
    local row = New("Frame", {
        Parent = parent, Size = UDim2.new(1, 0, 0, height or 44),
        BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.06,
    })
    Corner(row, 11)
    Gradient(row, {
        function(t) return Mix(t.ElementBG, t.Accent3, .07) end,
        "ElementBG",
        function(t) return Mix(t.ElementBG, t.Accent2, .065) end,
    }, 0)
    local outline = Stroke(row, "Border", 0.32)
    table.insert(page.Searchables, {Object = row, Text = string.lower(label)})
    row.MouseEnter:Connect(function()
        Tween(row, 0.16, {BackgroundColor3 = T.ElementHoverBG, BackgroundTransparency = 0})
    end)
    row.MouseLeave:Connect(function()
        Tween(row, 0.16, {BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.06})
    end)
    BindTheme(function()
        if row.Parent then row.BackgroundColor3 = T.ElementBG; outline.Color = T.Border end
    end)
    return row
end

local function Toggle(page, parentCard, label, default, onChanged)
    local state = default == true
    local row = ElementBase(page, parentCard, label, 44)
    local text = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(1, -72, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local track = New("Frame", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -12, 0.5, 0),
        Size = UDim2.fromOffset(38, 20), BackgroundColor3 = state and T.Accent or T.ControlBG,
    })
    Corner(track, 10)
    Gradient(track, {"Accent", "Accent2"}, 0)
    local knob = New("Frame", {
        Parent = track, AnchorPoint = Vector2.new(0, 0.5),
        Position = state and UDim2.new(1, -18, 0.5, 0) or UDim2.new(0, 3, 0.5, 0),
        Size = UDim2.fromOffset(15, 15), BackgroundColor3 = state and T.MainBG or T.TextSub,
    })
    Corner(knob, 8)
    local click = New("TextButton", {
        Parent = row, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
        Text = "", AutoButtonColor = false, ZIndex = 4,
    })
    local function Paint(animate)
        local duration = animate and 0.18 or 0
        Tween(track, duration, {BackgroundColor3 = state and T.Accent or T.ControlBG})
        Tween(knob, duration, {
            Position = state and UDim2.new(1, -18, 0.5, 0) or UDim2.new(0, 3, 0.5, 0),
            BackgroundColor3 = state and T.MainBG or T.TextSub,
        })
    end
    local function SetState(value, animate, fire)
        state = value == true
        Paint(animate)
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, state) end)
        end
    end
    click.MouseButton1Click:Connect(function()
        SetState(not state, true, true)
    end)
    BindTheme(function()
        if row.Parent then text.TextColor3 = T.TextMain; Paint(false) end
    end)
    return {
        Get = function() return state end,
        Set = function(value, fire) SetState(value, true, fire == nil and true or fire) end,
    }
end

local function Slider(page, parentCard, label, min, max, value, suffix, onChanged)
    local row = ElementBase(page, parentCard, label, 62)
    local current = value or min
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 5), Size = UDim2.new(1, -88, 0, 22),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local valueText = New("TextLabel", {
        Parent = row, AnchorPoint = Vector2.new(1, 0), Position = UDim2.new(1, -12, 0, 6),
        Size = UDim2.fromOffset(66, 20), BackgroundTransparency = 1,
        Text = tostring(current) .. (suffix or ""), Font = Enum.Font.GothamSemibold,
        TextSize = 10, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Right,
    })
    local track = New("TextButton", {
        Parent = row, Position = UDim2.new(0, 13, 1, -19), Size = UDim2.new(1, -26, 0, 7),
        BackgroundColor3 = T.ControlBG, Text = "", AutoButtonColor = false,
    })
    Corner(track, 4)
    local fill = New("Frame", {
        Parent = track, Size = UDim2.new((current - min) / math.max(max - min, 0.0001), 0, 1, 0),
        BackgroundColor3 = T.Accent, BorderSizePixel = 0,
    })
    Corner(fill, 4)
    Gradient(fill, {"Accent3", "Accent", "Accent2"}, 0)
    local knob = New("Frame", {
        Parent = fill, AnchorPoint = Vector2.new(0.5, 0.5), Position = UDim2.fromScale(1, 0.5),
        Size = UDim2.fromOffset(13, 13), BackgroundColor3 = T.TextMain,
    })
    Corner(knob, 7)
    Stroke(knob, "Accent", 0, 2)
    local dragging = false
    local function SetValue(newValue, fire)
        current = math.clamp(math.floor((newValue or min) * 10 + 0.5) / 10, min, max)
        local alpha = (current - min) / math.max(max - min, 0.0001)
        fill.Size = UDim2.new(alpha, 0, 1, 0)
        valueText.Text = tostring(current) .. (suffix or "")
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, current) end)
        end
    end
    local function Update(inputX)
        local alpha = math.clamp((inputX - track.AbsolutePosition.X) / track.AbsoluteSize.X, 0, 1)
        SetValue(min + ((max - min) * alpha), true)
    end
    track.InputBegan:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
            dragging = true
            Update(input.Position.X)
        end
    end)
    table.insert(connections, UserInputService.InputChanged:Connect(function(input)
        if dragging and (input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch) then
            Update(input.Position.X)
        end
    end))
    table.insert(connections, UserInputService.InputEnded:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then dragging = false end
    end))
    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain
            valueText.TextColor3 = T.Accent
            track.BackgroundColor3 = T.ControlBG
            fill.BackgroundColor3 = T.Accent
            knob.BackgroundColor3 = T.TextMain
        end
    end)
    return {
        Get = function() return current end,
        Set = function(v, fire) SetValue(v, fire == nil and true or fire) end,
    }
end

local activeDropdown
local function Dropdown(page, parentCard, label, options, default, onChanged)
    local selected = default or options[1]
    local row = ElementBase(page, parentCard, label, 44)
    row.ClipsDescendants = false
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(0.48, 0, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local box = New("TextButton", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -10, 0.5, 0),
        Size = UDim2.fromOffset(132, 28), BackgroundColor3 = T.ControlBG,
        Text = selected, TextColor3 = T.Accent, Font = Enum.Font.GothamMedium,
        TextSize = 10, AutoButtonColor = false, ZIndex = 10,
    })
    Corner(box, 9)
    Stroke(box, "Border", 0.35)

    local menuHeight = math.min(#options * 31 + 12, 205)
    local menu = New("ScrollingFrame", {
        Parent = DropdownLayer, Size = UDim2.fromOffset(170, menuHeight),
        BackgroundColor3 = T.SectionBG, Visible = false, ZIndex = 400,
        BorderSizePixel = 0, ScrollBarThickness = 3, ScrollBarImageColor3 = T.Accent,
        CanvasSize = UDim2.new(), AutomaticCanvasSize = Enum.AutomaticSize.Y,
        Active = true, ScrollingEnabled = true, ScrollingDirection = Enum.ScrollingDirection.Y,
    })
    Corner(menu, 12)
    Stroke(menu, "Border", 0.08)
    New("UIPadding", {
        Parent = menu, PaddingTop = UDim.new(0, 6), PaddingBottom = UDim.new(0, 6),
        PaddingLeft = UDim.new(0, 6), PaddingRight = UDim.new(0, 6),
    })
    New("UIListLayout", {Parent = menu, Padding = UDim.new(0, 3), SortOrder = Enum.SortOrder.LayoutOrder})

    local function PositionMenu()
        local windowPos = Window.AbsolutePosition
        local layerSize = DropdownLayer.AbsoluteSize
        local menuWidth = menu.AbsoluteSize.X > 0 and menu.AbsoluteSize.X or 170
        local finalHeight = menuHeight
        local x = (box.AbsolutePosition.X - windowPos.X) + box.AbsoluteSize.X - menuWidth
        local y = (box.AbsolutePosition.Y - windowPos.Y) + box.AbsoluteSize.Y + 6
        if y + finalHeight > layerSize.Y - 8 then
            y = (box.AbsolutePosition.Y - windowPos.Y) - finalHeight - 6
        end
        x = math.clamp(x, 8, math.max(8, layerSize.X - menuWidth - 8))
        y = math.clamp(y, 8, math.max(8, layerSize.Y - finalHeight - 8))
        menu.Position = UDim2.fromOffset(x, y)
    end

    for _, option in ipairs(options) do
        local item = New("TextButton", {
            Parent = menu, Size = UDim2.new(1, 0, 0, 28), BackgroundColor3 = T.ElementBG,
            BackgroundTransparency = 1, Text = option, TextColor3 = T.TextSub,
            Font = Enum.Font.Gotham, TextSize = 10, AutoButtonColor = false, ZIndex = 401,
        })
        Corner(item, 8)
        item.MouseEnter:Connect(function() Tween(item, 0.12, {BackgroundTransparency = 0, TextColor3 = T.TextMain}) end)
        item.MouseLeave:Connect(function() Tween(item, 0.12, {BackgroundTransparency = 1, TextColor3 = T.TextSub}) end)
        item.MouseButton1Click:Connect(function()
            selected = option; box.Text = option; menu.Visible = false; activeDropdown = nil; DropdownLayer.Visible = false
            if onChanged then onChanged(option) end
        end)
        BindTheme(function()
            if item.Parent then item.BackgroundColor3 = T.ElementBG; item.TextColor3 = T.TextSub end
        end)
    end
    box.MouseButton1Click:Connect(function()
        if activeDropdown and activeDropdown ~= menu then activeDropdown.Visible = false end
        PositionMenu()
        menu.Visible = not menu.Visible
        DropdownLayer.Visible = menu.Visible
        activeDropdown = menu.Visible and menu or nil
    end)
    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain; box.BackgroundColor3 = T.ControlBG
            box.TextColor3 = T.Accent; menu.BackgroundColor3 = T.SectionBG
            menu.ScrollBarImageColor3 = T.Accent
        end
    end)
    local function SetSelected(value, fire)
        if value == nil then return end
        selected = value
        box.Text = tostring(value)
        if fire and onChanged then
            task.spawn(function() pcall(onChanged, selected) end)
        end
    end
    return {
        Get = function() return selected end,
        Set = function(value, fire) SetSelected(value, fire == nil and true or fire) end,
        Refresh = function(newOptions)
            if type(newOptions) ~= "table" then return end
            options = newOptions
            for _, child in ipairs(menu:GetChildren()) do
                if child:IsA("TextButton") then child:Destroy() end
            end
            for _, option in ipairs(options) do
                local optionButton = New("TextButton", {
                    Parent = menu, Size = UDim2.new(1, -8, 0, 28), BackgroundColor3 = T.ElementBG,
                    Text = "  " .. tostring(option), TextColor3 = T.TextMain, Font = Enum.Font.Gotham,
                    TextSize = 10, TextXAlignment = Enum.TextXAlignment.Left, AutoButtonColor = false, ZIndex = 60,
                })
                Corner(optionButton, 8)
                optionButton.MouseButton1Click:Connect(function()
                    SetSelected(option, true)
                    menu.Visible = false
                    DropdownLayer.Visible = false
                    activeDropdown = nil
                end)
            end
            menu.CanvasSize = UDim2.new(0, 0, 0, #options * 32)
        end,
    }
end

local function Button(page, parentCard, label, emphasized, action)
    -- FIXED v2: Complete rewrite to avoid ElementBase double-hover conflict
    -- Previously Button called ElementBase which added its own hover tween to ElementHoverBG,
    -- causing emphasized buttons (Accent bg + MainBG text) to become invisible on hover.
    -- This version creates its own row with proper contrast for ALL themes.
    local row = New("Frame", {
        Parent = parentCard, Size = UDim2.new(1, 0, 0, 42),
        BackgroundColor3 = emphasized and T.Accent or T.ElementBG,
        BackgroundTransparency = emphasized and 0.08 or 0.06,
    })
    Corner(row, 11)
    -- Only non-emphasized buttons keep subtle gradient, emphasized stays solid accent for readability
    if not emphasized then
        Gradient(row, {
            function(t) return Mix(t.ElementBG, t.Accent3, .07) end,
            "ElementBG",
            function(t) return Mix(t.ElementBG, t.Accent2, .065) end,
        }, 0)
    end
    local outline = Stroke(row, "Border", 0.32)
    table.insert(page.Searchables, {Object = row, Text = string.lower(label)})

    local function getAccentTextColor()
        local lum = 0.299 * T.Accent.R + 0.587 * T.Accent.G + 0.114 * T.Accent.B
        if lum > 0.55 then
            return Color3.fromRGB(28, 30, 38) -- dark text for light accents (Tokyo Night #7AA2F7, Dracula, Rose Pine)
        else
            return Color3.fromRGB(255, 255, 255) -- white for dark accents (Aurora Gray, Midnight Violet, etc.)
        end
    end
    local function getAccentHover()
        return T.Accent:Lerp(Color3.new(0, 0, 0), 0.18)
    end

    local text = New("TextLabel", {
        Parent = row, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
        Text = label, Font = Enum.Font.GothamSemibold, TextSize = 11,
        TextColor3 = emphasized and getAccentTextColor() or T.TextMain,
    })
    local click = New("TextButton", {
        Parent = row, Size = UDim2.fromScale(1, 1), BackgroundTransparency = 1,
        Text = "", AutoButtonColor = false, ZIndex = 4,
    })
    click.MouseButton1Down:Connect(function() Tween(row, 0.08, {Size = UDim2.new(1, -4, 0, 40), Position = UDim2.fromOffset(2, 1)}) end)
    click.MouseButton1Up:Connect(function() Tween(row, 0.14, {Size = UDim2.new(1, 0, 0, 42), Position = UDim2.new()}) end)
    click.MouseButton1Click:Connect(function()
        if action then action() else Toast("UI Preview", label .. " is visual-only in this mockup.") end
    end)
    row.MouseEnter:Connect(function()
        if emphasized then
            Tween(row, 0.16, {BackgroundColor3 = getAccentHover(), BackgroundTransparency = 0})
        else
            Tween(row, 0.16, {BackgroundColor3 = T.ElementHoverBG, BackgroundTransparency = 0})
        end
    end)
    row.MouseLeave:Connect(function()
        if emphasized then
            Tween(row, 0.16, {BackgroundColor3 = T.Accent, BackgroundTransparency = 0.08})
        else
            Tween(row, 0.16, {BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.06})
        end
    end)
    BindTheme(function()
        if row.Parent then
            if emphasized then
                row.BackgroundColor3 = T.Accent
                text.TextColor3 = getAccentTextColor()
            else
                row.BackgroundColor3 = T.ElementBG
                text.TextColor3 = T.TextMain
            end
            outline.Color = T.Border
        end
    end)
end

local function Textbox(page, parentCard, label, placeholder, default, onChanged)
    local row = ElementBase(page, parentCard, label, 50)
    local title = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(13, 0), Size = UDim2.new(0.42, 0, 1, 0),
        BackgroundTransparency = 1, Text = label, Font = Enum.Font.Gotham,
        TextSize = 11, TextColor3 = T.TextMain, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local box = New("TextBox", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -10, 0.5, 0),
        Size = UDim2.fromOffset(150, 28), BackgroundColor3 = T.ControlBG,
        Text = tostring(default or ""), PlaceholderText = placeholder or "Enter text...",
        PlaceholderColor3 = T.TextSub, TextColor3 = T.TextMain, Font = Enum.Font.Gotham,
        TextSize = 10, ClearTextOnFocus = false, ZIndex = 10,
    })
    Corner(box, 9)
    Stroke(box, "Border", 0.35)
    box.FocusLost:Connect(function(enterPressed)
        if onChanged then
            task.spawn(function() pcall(onChanged, box.Text, enterPressed) end)
        end
    end)
    BindTheme(function()
        if row.Parent then
            title.TextColor3 = T.TextMain
            box.BackgroundColor3 = T.ControlBG
            box.TextColor3 = T.TextMain
            box.PlaceholderColor3 = T.TextSub
        end
    end)
    return {
        Get = function() return box.Text end,
        Set = function(text, fire)
            box.Text = tostring(text or "")
            if fire and onChanged then
                task.spawn(function() pcall(onChanged, box.Text, false) end)
            end
        end,
    }
end

local function StatGrid(page, stats)
    local grid = New("Frame", {
        Parent = page.Body, Size = UDim2.new(1, 0, 0, 92), BackgroundTransparency = 1,
    })
    local layout = New("UIGridLayout", {
        Parent = grid, CellPadding = UDim2.fromOffset(10, 0),
        CellSize = UDim2.new(0.25, -8, 1, 0), SortOrder = Enum.SortOrder.LayoutOrder,
    })
    for index, stat in ipairs(stats) do
        local card = New("Frame", {
            Parent = grid, LayoutOrder = index, BackgroundColor3 = T.SectionBG,
            BackgroundTransparency = 0.07,
        })
        Corner(card, 14); Gradient(card, {function(t) return Mix(t.SectionBG, t.Accent3, .12) end, function(t) return Mix(t.SectionBG, t.Accent2, .10) end}, 20); Stroke(card, "Border", 0.18)
        local value = New("TextLabel", {
            Parent = card, Position = UDim2.fromOffset(14, 14), Size = UDim2.new(1, -28, 0, 28),
            BackgroundTransparency = 1, Text = stat[2], Font = Enum.Font.GothamBold,
            TextSize = 21, TextColor3 = T.Accent, TextXAlignment = Enum.TextXAlignment.Left,
        })
        local label = New("TextLabel", {
            Parent = card, Position = UDim2.fromOffset(14, 48), Size = UDim2.new(1, -28, 0, 18),
            BackgroundTransparency = 1, Text = stat[1], Font = Enum.Font.Gotham,
            TextSize = 9, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
        })
        BindTheme(function()
            if card.Parent then card.BackgroundColor3 = T.SectionBG; value.TextColor3 = T.Accent; label.TextColor3 = T.TextSub end
        end)
    end
    return layout
end

local function TwoColumnRow(page)
    local row = New("Frame", {
        Parent = page.Body, Size = UDim2.new(1, 0, 0, 0),
        BackgroundTransparency = 1,
    })
    New("UIListLayout", {
        Parent = row, FillDirection = Enum.FillDirection.Horizontal, Padding = UDim.new(0, 14),
        SortOrder = Enum.SortOrder.LayoutOrder,
    })
    local left = New("Frame", {
        Parent = row, Size = UDim2.new(0.5, -20, 0, 0), AutomaticSize = Enum.AutomaticSize.Y,
        BackgroundTransparency = 1,
    })
    local leftLayout = New("UIListLayout", {Parent = left, Padding = UDim.new(0, 14), SortOrder = Enum.SortOrder.LayoutOrder})
    local right = New("Frame", {
        Parent = row, Size = UDim2.new(0.5, -20, 0, 0), AutomaticSize = Enum.AutomaticSize.Y,
        BackgroundTransparency = 1,
    })
    local rightLayout = New("UIListLayout", {Parent = right, Padding = UDim.new(0, 14), SortOrder = Enum.SortOrder.LayoutOrder})

    local function UpdateRowHeight()
        local height = math.max(leftLayout.AbsoluteContentSize.Y, rightLayout.AbsoluteContentSize.Y)
        row.Size = UDim2.new(1, 0, 0, height)
    end
    UpdateRowHeight()
    table.insert(connections, leftLayout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(UpdateRowHeight))
    table.insert(connections, rightLayout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(UpdateRowHeight))

    return left, right
end

local function HomeInfoRow(parent, labelText, valueText, accentValue)
    local row = New("Frame", {
        Parent = parent, Size = UDim2.new(1, 0, 0, 38),
        BackgroundColor3 = T.ElementBG, BackgroundTransparency = 0.03,
    })
    Corner(row, 10)
    Gradient(row, {
        function(t) return Mix(t.ElementBG, t.Accent3, .06) end,
        "ElementBG",
        function(t) return Mix(t.ElementBG, t.Accent2, .05) end,
    }, 0)
    Stroke(row, "Border", 0.28)
    local label = New("TextLabel", {
        Parent = row, Position = UDim2.fromOffset(12, 0), Size = UDim2.new(0.42, 0, 1, 0),
        BackgroundTransparency = 1, Text = labelText, Font = Enum.Font.Gotham,
        TextSize = 10, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
    })
    local value = New("TextLabel", {
        Parent = row, AnchorPoint = Vector2.new(1, 0.5), Position = UDim2.new(1, -12, 0.5, 0),
        Size = UDim2.new(0.55, 0, 1, 0), BackgroundTransparency = 1, Text = valueText,
        Font = Enum.Font.GothamSemibold, TextSize = 10,
        TextColor3 = accentValue and T.Accent or T.TextMain,
        TextXAlignment = Enum.TextXAlignment.Right, TextTruncate = Enum.TextTruncate.AtEnd,
    })
    BindTheme(function()
        if row.Parent then
            row.BackgroundColor3 = T.ElementBG
            label.TextColor3 = T.TextSub
            value.TextColor3 = accentValue and T.Accent or T.TextMain
        end
    end)
    return {Frame = row, Label = label, Value = value}
end

local function HomeStatCard(parent, order, titleText, valueText, accentValue)
    local card = New("Frame", {
        Parent = parent, LayoutOrder = order, BackgroundColor3 = T.SectionBG,
        BackgroundTransparency = 0.05,
    })
    Corner(card, 14)
    Gradient(card, {
        function(t) return Mix(t.SectionBG, t.Accent3, .12) end,
        function(t) return Mix(t.SectionBG, t.Accent2, .10) end,
    }, 20)
    Stroke(card, "Border", 0.18)
    local value = New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(14, 16), Size = UDim2.new(1, -28, 0, 28),
        BackgroundTransparency = 1, Text = valueText, Font = Enum.Font.GothamBold,
        TextSize = 16, TextColor3 = accentValue and T.Accent or T.TextMain,
        TextXAlignment = Enum.TextXAlignment.Left, TextTruncate = Enum.TextTruncate.AtEnd,
    })
    local title = New("TextLabel", {
        Parent = card, Position = UDim2.fromOffset(14, 52), Size = UDim2.new(1, -28, 0, 18),
        BackgroundTransparency = 1, Text = titleText, Font = Enum.Font.Gotham,
        TextSize = 9, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
    })
    BindTheme(function()
        if card.Parent then
            card.BackgroundColor3 = T.SectionBG
            value.TextColor3 = accentValue and T.Accent or T.TextMain
            title.TextColor3 = T.TextSub
        end
    end)
    return {Frame = card, Title = title, Value = value}
end

-- Pages --------------------------------------------------------------------
local Home = CreatePage("Home")
local homeStats = New("Frame", {
    Parent = Home.Body, Size = UDim2.new(1, 0, 0, 92), BackgroundTransparency = 1,
})
New("UIGridLayout", {
    Parent = homeStats, CellPadding = UDim2.fromOffset(10, 0),
    CellSize = UDim2.new(0.25, -8, 1, 0), SortOrder = Enum.SortOrder.LayoutOrder,
})
local homeNameStat = HomeStatCard(homeStats, 1, "SCRIPT", "BLADE BALL LYRA", true)
local homeExecutorStat = HomeStatCard(homeStats, 2, "EXECUTOR", executorName, false)
local homeThemeStat = HomeStatCard(homeStats, 3, "THEME", themeName, false)
local homeStatusStat = HomeStatCard(homeStats, 4, "STATUS", "LOADED", true)

local session = Section(Home, "Session details", "Current interface information.")
local sessionName = HomeInfoRow(session, "Script", "Blade Ball Lyra", true)
local sessionExecutor = HomeInfoRow(session, "Executor", executorName, false)
local sessionTheme = HomeInfoRow(session, "Theme", themeName, false)
local sessionStatus = HomeInfoRow(session, "Status", "Loaded", true)
local sessionPlayer = HomeInfoRow(session, "Player", LocalPlayer.DisplayName, false)

local runtime = Section(Home, "Status", "Live UI state and layout info.")
local runtimeScale = HomeInfoRow(runtime, "Responsive Scale", "Enabled", true)
local runtimeSidebar = HomeInfoRow(runtime, "Sidebar", "Hover Expand", false)
local runtimeSearch = HomeInfoRow(runtime, "Search", "Ready", false)
local runtimeTheme = HomeInfoRow(runtime, "Default Palette", themeName, false)
local runtimeMode = HomeInfoRow(runtime, "Mode", "Feature Build", false)
local runtimeWindow = HomeInfoRow(runtime, "Ping", "0 ms", false)

table.insert(connections, RunService.RenderStepped:Connect(function()
    if homeStatusStat and homeStatusStat.Value and homeStatusStat.Value.Parent then
        homeStatusStat.Value.Text = System.__properties.__autoparry_enabled and "ARMED" or "READY"
    end
    if sessionStatus and sessionStatus.Value and sessionStatus.Value.Parent then
        sessionStatus.Value.Text = System.__triggerbot.__enabled and "Triggerbot" or "Loaded"
    end
    if runtimeMode and runtimeMode.Value and runtimeMode.Value.Parent then
        runtimeMode.Value.Text = System.__properties.__manual_spam_enabled and "Spam Active" or "Feature Build"
    end
    if runtimeWindow and runtimeWindow.Value and runtimeWindow.Value.Parent then
        runtimeWindow.Value.Text = tostring(safePing()) .. " ms"
    end
end))

BindTheme(function()
    if homeThemeStat and homeThemeStat.Value.Parent then homeThemeStat.Value.Text = themeName end
    if sessionTheme and sessionTheme.Value.Parent then sessionTheme.Value.Text = themeName end
    if runtimeTheme and runtimeTheme.Value.Parent then runtimeTheme.Value.Text = themeName end
end)

local pageDefinitions = {
    Combat = {
        {"Autoparry", "Core parry logic, trigger modes, and ability support.", {
            {"toggle", "Enable Autoparry", false, function(state)
                System.__properties.__autoparry_enabled = state
                if state then
                    System.autoparry.start()
                    notify("Autoparry", "Enabled", 3)
                else
                    System.autoparry.stop()
                    notify("Autoparry", "Disabled", 3)
                end
            end},
            {"dropdown", "Parry Method", {"Combined (Mobile)", "Screen Tap", "Block Button", "Direct Call (PF)", "KeyPress (F)", "MouseClick"}, getgenv().AutoParryMode or "Combined (Mobile)", function(value)
                getgenv().AutoParryMode = value
                notify("Parry Method", value, 2.5)
            end},
            {"toggle", "Triggerbot", false, function(state)
                System.triggerbot.enable(state)
                notify("Triggerbot", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Accuracy", 1, 10, 1, "", function(value)
                System.__properties.__accuracy = value
                update_divisor()
            end},
            {"toggle", "Play Parry Animation", false, function(state)
                System.__properties.__play_animation = state
            end},
            {"toggle", "Cooldown Protection", false, function(state)
                getgenv().CooldownProtection = state
            end},
            {"toggle", "Auto Ability", false, function(state)
                getgenv().AutoAbility = state
            end},
        }},
        {"Detections", "Handle Infinity, Death Slash, Time Hole, Slashes of Fury, and Phantom checks.", {
            {"toggle", "Infinity Detection", false, function(state) System.__config.__detections.__infinity = state end},
            {"toggle", "Deathslash Detection", false, function(state) System.__config.__detections.__deathslash = state end},
            {"toggle", "Timehole Detection", false, function(state) System.__config.__detections.__timehole = state end},
            {"toggle", "Slashes Of Fury Detection", false, function(state) System.__config.__detections.__slashesoffury = state end},
            {"toggle", "Phantom Detection", false, function(state) System.__config.__detections.__phantom = state end},
        }},
        {"Spam", "Manual, hold, and automatic spam controls.", {
            {"toggle", "Always Spam", false, function(state)
                System.__properties.__manual_spam_enabled = state
                notify("Manual Spam", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Hold To Spam", false, function(state)
                getgenv().HoldSpamEnabled = state
                notify("Hold Spam", state and "Enabled" or "Disabled", 3)
            end},
            {"textbox", "Hold Spam Key", "V", getgenv().HoldSpamKey or "V", function(value)
                local key = string.upper((value or "V"):gsub("%s+", ""))
                if key == "" then key = "V" end
                getgenv().HoldSpamKey = key
            end},
            {"toggle", "Auto Spam", false, function(state)
                if state then
                    System.auto_spam.start()
                    notify("Auto Spam", "Enabled", 3)
                else
                    System.auto_spam.stop()
                    notify("Auto Spam", "Disabled", 3)
                end
            end},
            {"slider", "Spam Rate", 10, 300, 240, "", function(value)
                System.__properties.__spam_rate = value
            end},
        }},
    },
    Visuals = {
        {"ESP", "Highlight enemies and control simple visibility features.", {
            {"toggle", "Enable ESP", false, function(state)
                System.visuals.__esp_enabled = state
                notify("ESP", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Team Check", false, function(state)
                System.visuals.__esp_team_check = state
            end},
        }},
        {"Environment", "Lighting and ball effect visuals.", {
            {"toggle", "Night Mode", false, function(state)
                System.visuals.toggle_night_mode(state)
                notify("Night Mode", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Rain Effect", false, function(state)
                Features.Visuals.Rain.Enabled = state
                notify("Rain", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Max Rain Particles", 1000, 10000, 5000, "", function(value)
                Features.Visuals.Rain.MaxParticles = value
            end},
            {"slider", "Rain Fall Speed", 10, 50, 25, "", function(value)
                Features.Visuals.Rain.FallSpeed = value
            end},
            {"toggle", "Plasma Trails", false, function(state)
                Features.Visuals.Plasma.Enabled = state
                notify("Plasma", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Trail Count", 4, 16, 8, "", function(value)
                Features.Visuals.Plasma.NumTrails = math.floor(value)
            end},
        }},
    },
    Player = {
        {"Movement", "WalkSpeed and JumpPower modifiers.", {
            {"toggle", "Custom WalkSpeed", false, function(state) System.player_mods.__walkspeed_enabled = state end},
            {"slider", "WalkSpeed", 16, 100, 16, "", function(value) System.player_mods.__walkspeed_value = value end},
            {"toggle", "Custom JumpPower", false, function(state) System.player_mods.__jumppower_enabled = state end},
            {"slider", "JumpPower", 50, 200, 50, "", function(value) System.player_mods.__jumppower_value = value end},
        }},
        {"Camera", "FOV, spin, and camera shake options.", {
            {"toggle", "Disable Cam Shake", false, function(state) System.player_mods.disable_camera_shake(state) end},
            {"toggle", "FOV Mod", false, function(state) System.player_mods.__fov_enabled = state end},
            {"slider", "FOV", 70, 120, 70, "", function(value) System.player_mods.__fov_value = value end},
            {"toggle", "Spinbot", false, function(state)
                System.player_mods.__spinbot_enabled = state
                notify("Spinbot", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Spin Speed", 10, 100, 50, "", function(value) System.player_mods.__spinbot_speed = value end},
        }},
    },
    Utility = {
        {"Emotes", "Play and manage available emotes.", {
            {"toggle", "Enable Emote System", false, function(state)
                if state then
                    animation_system.start()
                    notify("Emote System", "Enabled", 3)
                else
                    animation_system.cleanup()
                    notify("Emote System", "Disabled", 3)
                end
            end},
            {"textbox", "Search Emote", "Type and press enter", "", function(value, enterPressed)
                if not enterPressed or value == "" then return end
                local lower = string.lower(value)
                for _, emote in ipairs(animation_system.get_emotes_list()) do
                    if string.lower(emote):find(lower, 1, true) then
                        animation_system.play(emote)
                        animation_system.current = emote
                        notify("Emote", "Playing: " .. emote, 3)
                        return
                    end
                end
                notify("Emote", "No emote matched that search.", 3)
            end},
            {"dropdown", "Select Emote", emotes_data, emotes_data[1] or "None", function(value)
                if value ~= "None" then
                    animation_system.play(value)
                    animation_system.current = value
                    notify("Emote", "Playing: " .. value, 3)
                end
            end},
            {"toggle", "Auto Stop On Move", getgenv().AutoStopEmote == true, function(state)
                getgenv().AutoStopEmote = state
            end},
            {"button", "Stop Current Emote", false, function()
                animation_system.stop()
                notify("Emote", "Stopped", 2.5)
            end},
        }},
        {"Skin Changer", "Local sword model, animation, and FX customization.", {
            {"toggle", "Enable Skin Changer", false, function(state)
                getgenv().skinChangerEnabled = state
                if state and getgenv().updateSword then pcall(getgenv().updateSword) end
                notify("Skin Changer", state and "Enabled" or "Disabled", 3)
            end},
            {"toggle", "Change Sword Model", false, function(state) getgenv().changeSwordModel = state end},
            {"textbox", "Sword Model Name", "Ex: Excalibur", getgenv().swordModel or "", function(value) getgenv().swordModel = value end},
            {"toggle", "Change Sword Animation", false, function(state) getgenv().changeSwordAnimation = state end},
            {"textbox", "Sword Anim Name", "Ex: DualWield", getgenv().swordAnimations or "", function(value) getgenv().swordAnimations = value end},
            {"toggle", "Change Sword FX", false, function(state) getgenv().changeSwordFX = state end},
            {"textbox", "Sword FX Name", "Ex: Fire", getgenv().swordFX or "", function(value) getgenv().swordFX = value end},
            {"button", "Refresh Sword Visuals", false, function()
                if getgenv().updateSword then
                    pcall(getgenv().updateSword)
                    notify("Skin Changer", "Refreshed sword visuals.", 3)
                else
                    notify("Skin Changer", "No sword refresh function found.", 3)
                end
            end},
        }},
    },
    Exclusive = {
        {"Walkable Immortal", "Semi-immortal desync controls.", {
            {"toggle", "Enable Walkable Immortal", false, function(state)
                WalkableSemiImmortal.toggle(state)
                notify("Walkable Immortal", state and "Enabled" or "Disabled", 3)
            end},
            {"slider", "Radius", 0, 100, 25, "", function(value) WalkableSemiImmortal.setRadius(value) end},
            {"slider", "Height", 0, 60, 30, "", function(value) WalkableSemiImmortal.setHeight(value) end},
        }},
        {"Target Player", "Pick a player by username or display name.", {
            {"toggle", "Enable Target Player", false, function(state)
                Features.TargetPlayer.Enabled = state
                notify("Target Player", state and "Enabled" or "Disabled", 3)
            end},
            {"textbox", "Player Name", "Username or display name", "", function(value)
                Features.TargetPlayer.setTarget(value)
            end},
        }},
        {"Avatar Changer", "Apply another player's avatar onto your character locally.", {
            {"textbox", "Target Username", "Username...", getgenv().PendingAvatarTarget or "", function(value)
                getgenv().PendingAvatarTarget = value
            end},
            {"button", "Apply Avatar", true, function()
                local target = getgenv().PendingAvatarTarget or ""
                if target == "" then
                    notify("Avatar Changer", "Enter a username first.", 3)
                    return
                end
                notify("Avatar Changer", "Attempting to change avatar...", 3)
                Features.AvatarChanger.setAvatar(target)
            end},
        }},
    },
}

local function AddControl(page, card, definition)
    local control
    if definition[1] == "toggle" then
        control = Toggle(page, card, definition[2], definition[3], definition[4])
    elseif definition[1] == "slider" then
        control = Slider(page, card, definition[2], definition[3], definition[4], definition[5], definition[6], definition[7])
    elseif definition[1] == "dropdown" then
        control = Dropdown(page, card, definition[2], definition[3], definition[4], definition[5])
    elseif definition[1] == "button" then
        control = Button(page, card, definition[2], definition[3], definition[4])
    elseif definition[1] == "textbox" then
        control = Textbox(page, card, definition[2], definition[3], definition[4], definition[5])
    end
    if control and type(control) == "table" and definition[2] then
        ConfigManager.Register(definition[2], control)
    end
    return control
end

for pageName, sections in pairs(pageDefinitions) do
    local page = CreatePage(pageName)
    for _, definition in ipairs(sections) do
        local card = Section(page, definition[1], definition[2])
        for _, control in ipairs(definition[3]) do
            AddControl(page, card, control)
        end
    end
end

local Settings = CreatePage("Settings")
local appearance = Section(Settings, "Appearance", "Theme and accent presentation.")
ConfigManager.Register("UI Theme", Dropdown(Settings, appearance, "UI Theme", {"Midnight Violet", "Rose Pine", "Catppuccin", "Dracula", "Tokyo Night", "Minimal White", "Aurora Gray", "Hello Kitty"}, themeName, function(value)
    themeName = value; T = Themes[value]
    if homeThemeStat and homeThemeStat.Value.Parent then homeThemeStat.Value.Text = value end
    if sessionTheme and sessionTheme.Value.Parent then sessionTheme.Value.Text = value end
    if runtimeTheme and runtimeTheme.Value.Parent then runtimeTheme.Value.Text = value end
    for _, repaint in ipairs(themeBindings) do pcall(repaint) end
    Toast("Theme changed", value .. " palette applied to the mockup.")
end))
ConfigManager.Register("Rainbow Accent", Toggle(Settings, appearance, "Rainbow Accent", false))
ConfigManager.Register("Rainbow Speed", Slider(Settings, appearance, "Rainbow Speed", 1, 20, 5, ""))
ConfigManager.Register("Window Scale", Slider(Settings, appearance, "Window Scale", 50, 150, 100, "%", function(value)
    uiScaleFactor = math.clamp(value / 100, 0.55, 1.25)
    task.defer(function()
        if Scale and GetTargetScale then
            Scale.Scale = GetTargetScale()
        end
    end)
end))
local behavior = Section(Settings, "Window & navigation", "General interface preferences.")
ConfigManager.Register("Notifications & Tooltips", Toggle(Settings, behavior, "Notifications & Tooltips", true, function(value)
    notificationsEnabled = value
end))
ConfigManager.Register("Expand Sidebar on Hover", Toggle(Settings, behavior, "Expand Sidebar on Hover", true, function(value)
    sidebarHoverEnabled = value
    if not value then
        SetSidebar(true)
    end
end))
ConfigManager.Register("Menu Keybind", Dropdown(Settings, behavior, "Menu Keybind", {"RightControl", "Insert", "Home", "F4"}, "RightControl", function(value)
    menuToggleKey = Enum.KeyCode[value] or Enum.KeyCode.RightControl
    notify("Menu Keybind", value, 2.5)
end))
Button(Settings, behavior, "Preview notification", true, function() notify("Blade Ball Lyra", "This is the connected notification style.", 3) end)

-- Per-script configs (Lyra/Blade Ball/Configs + Autoload.txt)
local configs = Section(Settings, "Configs", "Configs for " .. SCRIPT_ID .. " only — never shared with other Lyra scripts.")
local configName = "default"
local configNameBox = ConfigManager.Register("Config Name", Textbox(Settings, configs, "Config Name", "default", "default", function(text)
    configName = tostring(text or "default")
end))
local autoloadToggle
autoloadToggle = ConfigManager.Register("Autoload", Toggle(Settings, configs, "Autoload", ConfigManager.GetAutoload() ~= nil, function(state)
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    ConfigManager.SetAutoload(configName, state)
    notify("Autoload", state and ("Will load '" .. configName .. "' for " .. SCRIPT_ID) or "Autoload disabled", 3)
end))
local viewConfigs
local function refreshConfigDropdown()
    local list = ConfigManager.List()
    if #list == 0 then list = {"default"} end
    if viewConfigs and viewConfigs.Refresh then viewConfigs.Refresh(list) end
    return list
end
viewConfigs = ConfigManager.Register("View Configs", Dropdown(Settings, configs, "View Configs", refreshConfigDropdown(), "default", function(value)
    configName = value
    if configNameBox and configNameBox.Set then configNameBox.Set(value, false) end
    if autoloadToggle and autoloadToggle.Set then
        autoloadToggle.Set(ConfigManager.GetAutoload() == value, false)
    end
end))
Button(Settings, configs, "Save Config", true, function()
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    local ok, err = ConfigManager.Save(configName)
    refreshConfigDropdown()
    notify("Configs", ok and ("Saved '" .. configName .. "' → " .. ConfigManager.Folder) or tostring(err), 4)
end)
Button(Settings, configs, "Load Config", false, function()
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    local ok, res = ConfigManager.Load(configName)
    notify("Configs", ok and ("Loaded '" .. configName .. "' (" .. tostring(res) .. " flags)") or tostring(res), 4)
end)
Button(Settings, configs, "Delete Config", false, function()
    if configNameBox and configNameBox.Get then configName = configNameBox.Get() end
    local ok, err = ConfigManager.Delete(configName)
    refreshConfigDropdown()
    notify("Configs", ok and ("Deleted '" .. configName .. "'") or tostring(err), 3)
end)

-- Theme bindings for primary shell -----------------------------------------
BindTheme(function()
    if destroyed then return end
    Dim.BackgroundColor3 = T.MainBG
    Dim.BackgroundTransparency = 1
    Window.BackgroundColor3 = T.MainBG
    Sidebar.BackgroundColor3 = T.SidebarBG
    sideLine.BackgroundColor3 = T.Border
    Topbar.BackgroundColor3 = T.TopbarBG
    topLine.BackgroundColor3 = T.Border
    GlowOne.BackgroundColor3 = T.Accent
    BrandLogoShell.BackgroundColor3 = T.ElementBG
    BrandTitle.TextColor3 = T.TextMain; BrandSub.TextColor3 = T.Accent
    Profile.BackgroundColor3 = T.ElementBG; Avatar.BackgroundColor3 = T.ControlBG
    ProfileName.TextColor3 = T.TextMain; ProfileStatus.TextColor3 = T.Accent
    PageTitle.TextColor3 = T.TextMain; Breadcrumb.TextColor3 = T.TextSub
    SearchBox.BackgroundColor3 = T.ControlBG; SearchIcon.ImageColor3 = T.TextSub
    SearchInput.TextColor3 = T.TextMain; SearchInput.PlaceholderColor3 = T.TextSub
    MinimizeButton.BackgroundColor3 = T.ControlBG; MinimizeButton.ImageColor3 = T.TextSub
    CloseButton.BackgroundColor3 = T.ControlBG; CloseButton.ImageColor3 = T.TextSub
    OpenButton.BackgroundColor3 = T.MainBG
    windowStroke.Color = T.Border
end)

-- Navigation ---------------------------------------------------------------
local navItems = {
    {"Home", ICONS.Home},
    {"Combat", ICONS.Defense},
    {"Visuals", ICONS.Visual},
    {"Player", ICONS.Player},
    {"Utility", ICONS.Misc},
    {"Exclusive", ICONS.Trolling},
    {"Settings", ICONS.Settings},
}

SetSidebar = function(expanded)
    if sidebarExpanded == expanded then return end
    sidebarExpanded = expanded
    Tween(Sidebar, 0.28, {Size = UDim2.new(0, expanded and SIDEBAR_OPEN or SIDEBAR_COMPACT, 1, 0)})
    ApplyContentLayout(expanded, false)
    Tween(BrandTitle, 0.18, {TextTransparency = expanded and 0 or 1})
    Tween(BrandSub, 0.18, {TextTransparency = expanded and 0 or 1})
    Tween(ProfileName, 0.18, {TextTransparency = expanded and 0 or 1})
    Tween(ProfileStatus, 0.18, {TextTransparency = expanded and 0 or 1})
    for _, item in pairs(navEntries) do
        Tween(item.Label, 0.18, {TextTransparency = expanded and 0 or 1})
    end
end

local function SelectPage(name)
    local targetPage = pages[name]
    if not targetPage then return end
    if currentPage == name and targetPage.Group.Visible then return end

    if activeDropdown then activeDropdown.Visible = false; activeDropdown = nil end
    DropdownLayer.Visible = false

    local previousName = currentPage
    local previousPage = previousName and pages[previousName] or nil
    currentPage = name

    if previousPage and previousPage.Group.Visible then
        Tween(previousPage.Group, 0.18, {
            GroupTransparency = 1,
            Position = UDim2.fromOffset(-14, 0)
        })

        task.delay(0.19, function()
            if previousPage.Group and previousPage.Group.Parent then
                previousPage.Group.Visible = false
                previousPage.Group.GroupTransparency = 1
            end
        end)
    end

    targetPage.Group.Visible = true
    targetPage.Group.Position = UDim2.fromOffset(18, 0)
    targetPage.Group.GroupTransparency = 1

    if targetPage.Scroll then
        targetPage.Scroll.CanvasPosition = Vector2.new(0, 0)
    end

    -- With AutomaticCanvasSize = Y, the scroll frame handles canvas sizing automatically.
    -- No manual Refresh needed.

    Tween(targetPage.Group, 0.3, {
        GroupTransparency = 0,
        Position = UDim2.fromOffset(0, 0)
    })

    PageTitle.Text = name == "Home" and "Status" or name
    Breadcrumb.Text = name == "Home" and "Lyra  /  Status" or ("Lyra  /  " .. name)
    SearchInput.Text = ""

    for navName, item in pairs(navEntries) do
        local selected = navName == name
        Tween(item.Button, 0.18, {
            BackgroundTransparency = selected and 0.04 or 1,
            BackgroundColor3 = selected and T.ElementBG or T.SidebarBG,
        })
        Tween(item.Icon, 0.18, {ImageColor3 = selected and T.Accent or T.TextSub})
        Tween(item.Label, 0.18, {TextColor3 = selected and T.TextMain or T.TextSub})
        Tween(item.Indicator, 0.18, {BackgroundTransparency = selected and 0 or 1, Size = UDim2.fromOffset(3, selected and 22 or 4)})
    end
end

for index, definition in ipairs(navItems) do
    local name, icon = definition[1], definition[2]
    local button = New("TextButton", {
        Parent = NavScroll, LayoutOrder = index, Size = UDim2.new(1, 0, 0, 43),
        BackgroundColor3 = T.ElementBG, BackgroundTransparency = 1,
        Text = "", AutoButtonColor = false, ZIndex = 23,
    })
    Corner(button, 11)
    Gradient(button, {function(t) return Mix(t.ElementBG, t.Accent3, .16) end, function(t) return Mix(t.ElementBG, t.Accent2, .13) end}, 0)
    local indicator = New("Frame", {
        Parent = button, AnchorPoint = Vector2.new(0, 0.5), Position = UDim2.new(0, 0, 0.5, 0),
        Size = UDim2.fromOffset(3, 4), BackgroundColor3 = T.Accent,
        BackgroundTransparency = 1, BorderSizePixel = 0, ZIndex = 24,
    })
    Corner(indicator, 3)
    Gradient(indicator, {"Accent3", "Accent", "Accent2"}, 90)
    local image = New("ImageLabel", {
        Parent = button, Position = UDim2.fromOffset(14, 11), Size = UDim2.fromOffset(21, 21),
        BackgroundTransparency = 1, Image = icon, ImageColor3 = T.TextSub, ZIndex = 24,
    })
    local label = New("TextLabel", {
        Parent = button, Position = UDim2.fromOffset(48, 0), Size = UDim2.new(1, -52, 1, 0),
        BackgroundTransparency = 1, Text = name, Font = Enum.Font.GothamMedium,
        TextSize = 11, TextColor3 = T.TextSub, TextXAlignment = Enum.TextXAlignment.Left,
        TextTransparency = 1, ZIndex = 24,
    })
    navEntries[name] = {Button = button, Icon = image, Label = label, Indicator = indicator}
    button.MouseEnter:Connect(function()
        if currentPage ~= name then Tween(button, 0.15, {BackgroundTransparency = 0.55}) end
    end)
    button.MouseLeave:Connect(function()
        if currentPage ~= name then Tween(button, 0.15, {BackgroundTransparency = 1}) end
    end)
    button.MouseButton1Click:Connect(function() SelectPage(name) end)
    BindTheme(function()
        if button.Parent then
            local selected = currentPage == name
            button.BackgroundColor3 = selected and T.ElementBG or T.SidebarBG
            image.ImageColor3 = selected and T.Accent or T.TextSub
            label.TextColor3 = selected and T.TextMain or T.TextSub
            indicator.BackgroundColor3 = T.Accent
        end
    end)
end

Sidebar.MouseEnter:Connect(function() if sidebarHoverEnabled then SetSidebar(true) end end)
Sidebar.MouseLeave:Connect(function() if sidebarHoverEnabled then SetSidebar(false) end end)

SearchInput:GetPropertyChangedSignal("Text"):Connect(function()
    local query = string.lower(SearchInput.Text)
    local page = pages[currentPage]
    if not page then return end
    for _, searchable in ipairs(page.Searchables) do
        searchable.Object.Visible = query == "" or string.find(searchable.Text, query, 1, true) ~= nil
    end
end)

-- Dragging -----------------------------------------------------------------
local dragging, dragStart, startPosition, dragInput
Topbar.InputBegan:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
        dragging = true; dragStart = input.Position; startPosition = Window.Position
        input.Changed:Connect(function()
            if input.UserInputState == Enum.UserInputState.End then dragging = false end
        end)
    end
end)
Topbar.InputChanged:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch then dragInput = input end
end)
table.insert(connections, UserInputService.InputChanged:Connect(function(input)
    if dragging and input == dragInput then
        local delta = input.Position - dragStart
        Window.Position = UDim2.new(startPosition.X.Scale, startPosition.X.Offset + delta.X, startPosition.Y.Scale, startPosition.Y.Offset + delta.Y)
    end
end))

-- Window behavior ----------------------------------------------------------
local windowOpen = true
local function SetOpen(open)
    windowOpen = open
    if open then
        local targetScale = GetTargetScale()
        Window.Visible = true; Dim.Visible = true
        Window.GroupTransparency = 1; Scale.Scale = targetScale * 0.94
        Tween(Window, 0.28, {GroupTransparency = 0})
        Tween(Scale, 0.3, {Scale = targetScale})
        OpenButton.Visible = false
    else
        if activeDropdown then activeDropdown.Visible = false; activeDropdown = nil end
        DropdownLayer.Visible = false
        Tween(Window, 0.22, {GroupTransparency = 1})
        task.delay(0.22, function()
            if not windowOpen and Window.Parent then Window.Visible = false; Dim.Visible = false; OpenButton.Visible = true end
        end)
    end
end
MinimizeButton.MouseButton1Click:Connect(function() SetOpen(false) end)
OpenButton.MouseButton1Click:Connect(function() SetOpen(true) end)
OpenButton.MouseEnter:Connect(function() Tween(OpenButton, 0.16, {Size = UDim2.fromOffset(59, 59)}) end)
OpenButton.MouseLeave:Connect(function() Tween(OpenButton, 0.16, {Size = UDim2.fromOffset(54, 54)}) end)
CloseButton.MouseButton1Click:Connect(function()
    destroyed = true
    for _, connection in ipairs(connections) do pcall(function() connection:Disconnect() end) end
    Screen:Destroy()
end)

table.insert(connections, UserInputService.InputBegan:Connect(function(input, processed)
    if processed then return end
    if input.KeyCode == menuToggleKey then SetOpen(not windowOpen) end
end))

-- Responsive scale. Keeps the 1080×640 design but leaves more edge safety on smaller viewports.
local baseScale = 1
GetTargetScale = function()
    return math.clamp(baseScale * uiScaleFactor, 0.5, 1.15)
end
local function UpdateResponsiveScale()
    local camera = workspace.CurrentCamera
    if not camera then return end
    local viewport = camera.ViewportSize
    local fitX = (viewport.X - 72) / 900
    local fitY = (viewport.Y - 72) / 540
    baseScale = math.clamp(math.min(fitX, fitY, 1), 0.55, 1)
    if windowOpen then Scale.Scale = GetTargetScale() end
end
if workspace.CurrentCamera then
    table.insert(connections, workspace.CurrentCamera:GetPropertyChangedSignal("ViewportSize"):Connect(UpdateResponsiveScale))
end
UpdateResponsiveScale()

-- Initial state and entrance.
pages.Home.Group.Visible = true
pages.Home.Group.GroupTransparency = 0
ApplyContentLayout(false, true)
SelectPage("Home")
Window.GroupTransparency = 1
Scale.Scale = GetTargetScale() * 0.94
Tween(Window, 0.4, {GroupTransparency = 0})
Tween(Scale, 0.45, {Scale = GetTargetScale()})
task.delay(0.5, function()
    if Screen.Parent then Toast("Blade Ball Lyra", "Lyra redesign loaded and connected to Blade Ball features.") end
end)
task.spawn(function()
    task.wait(0.75)
    local autoName = ConfigManager.GetAutoload()
    if autoName then
        local ok, res = ConfigManager.LoadAutoload()
        if Screen.Parent then
            notify("Autoload", ok and ("Loaded '" .. autoName .. "' (" .. tostring(res) .. " flags)") or ("Failed: " .. tostring(res)), 4)
        end
    end
end)

end
